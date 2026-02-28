package com.flashback.app.trigger

import com.flashback.app.model.AppConstants
import java.util.Calendar

/**
 * 觸發決策引擎，實作 AND 條件邏輯。
 * 所有條件必須同時滿足才會觸發閃光。
 */
class TriggerEngine(
    var volumeThresholdDb: Double = AppConstants.DEFAULT_VOLUME_THRESHOLD_DB,
    var classificationConfidence: Float = AppConstants.DEFAULT_CLASSIFICATION_CONFIDENCE,
    var minDurationMs: Long = AppConstants.DEFAULT_MIN_DURATION_MS,
    var startHour: Int = AppConstants.DEFAULT_START_HOUR,
    var endHour: Int = AppConstants.DEFAULT_END_HOUR,
    var targetLabels: Set<String> = DEFAULT_TARGET_LABELS,
    var classificationEnabled: Boolean = true,
    var timeWindowEnabled: Boolean = true
) {
    companion object {
        /** 飆車相關的 YAMNet 標籤 */
        val DEFAULT_TARGET_LABELS = setOf(
            "Vehicle", "Car", "Engine", "Engine starting",
            "Accelerating, revving, vroom", "Race car, auto racing",
            "Motorcycle", "Tire squeal", "Skidding",
            "Traffic noise, roadway noise"
        )
    }

    private var exceedStartTimeMs: Long = 0L
    private var isExceeding: Boolean = false

    /**
     * 評估當前幀是否觸發。
     *
     * @param volumeDb 當前音量 dB
     * @param classificationLabel YAMNet 分類標籤（若未啟用分類可傳空字串）
     * @param classificationConf YAMNet 信心度（若未啟用分類可傳 0）
     * @param currentTimeMs 當前時間戳（用於計算持續時間）
     * @return 若觸發則回傳 TriggerEvent，否則回傳 null
     */
    fun evaluate(
        volumeDb: Double,
        classificationLabel: String = "",
        classificationConf: Float = 0f,
        currentTimeMs: Long = System.currentTimeMillis()
    ): TriggerEvent? {
        val conditions = mutableListOf<TriggerCondition>()

        // 1. 音量條件（必要）
        val volumeCond = VolumeCondition(volumeDb, volumeThresholdDb)
        conditions.add(volumeCond)

        // 2. 分類條件（可選）
        if (classificationEnabled) {
            val classifyCond = ClassificationCondition(
                classificationLabel, classificationConf,
                classificationConfidence, targetLabels
            )
            conditions.add(classifyCond)
        }

        // 3. 時段條件（可選）
        if (timeWindowEnabled) {
            val calendar = Calendar.getInstance().apply { timeInMillis = currentTimeMs }
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            conditions.add(TimeWindowCondition(currentHour, startHour, endHour))
        }

        // 檢查音量+分類+時段
        val basicConditionsMet = conditions.all { it.isMet() }

        // 4. 持續時間追蹤
        if (basicConditionsMet) {
            if (!isExceeding) {
                isExceeding = true
                exceedStartTimeMs = currentTimeMs
            }
            val duration = currentTimeMs - exceedStartTimeMs
            val durationCond = DurationCondition(duration, minDurationMs)
            if (durationCond.isMet()) {
                // 重置以避免連續觸發
                isExceeding = false
                return TriggerEvent(
                    timestampMs = currentTimeMs,
                    volumeDb = volumeDb,
                    classificationLabel = classificationLabel,
                    classificationConfidence = classificationConf,
                    durationMs = duration
                )
            }
        } else {
            isExceeding = false
        }

        return null
    }

    /** 重置內部狀態 */
    fun reset() {
        isExceeding = false
        exceedStartTimeMs = 0L
    }
}
