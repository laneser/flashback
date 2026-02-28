package com.flashback.app.trigger

/** 觸發條件封裝 */
sealed interface TriggerCondition {
    fun isMet(): Boolean
}

/** 音量條件：RMS dB 超過閾值 */
data class VolumeCondition(
    val currentDb: Double,
    val thresholdDb: Double
) : TriggerCondition {
    override fun isMet(): Boolean = currentDb >= thresholdDb
}

/** 分類條件：YAMNet 信心度超過閾值且為目標類別 */
data class ClassificationCondition(
    val label: String,
    val confidence: Float,
    val requiredConfidence: Float,
    val targetLabels: Set<String>
) : TriggerCondition {
    override fun isMet(): Boolean =
        confidence >= requiredConfidence && label in targetLabels
}

/** 持續時間條件：聲音持續超過最小時長 */
data class DurationCondition(
    val currentDurationMs: Long,
    val minDurationMs: Long
) : TriggerCondition {
    override fun isMet(): Boolean = currentDurationMs >= minDurationMs
}

/** 時段條件：在指定監聽時段內 */
data class TimeWindowCondition(
    val currentHour: Int,
    val startHour: Int,
    val endHour: Int
) : TriggerCondition {
    override fun isMet(): Boolean {
        return if (startHour <= endHour) {
            // 例：08:00 - 22:00
            currentHour in startHour until endHour
        } else {
            // 跨午夜，例：22:00 - 06:00
            currentHour >= startHour || currentHour < endHour
        }
    }
}
