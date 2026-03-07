package com.flashback.app.trigger

/** 觸發事件記錄 */
data class TriggerEvent(
    val timestampMs: Long = System.currentTimeMillis(),
    val volumeDb: Double,
    val classificationLabel: String = "",
    val classificationConfidence: Float = 0f,
    val durationMs: Long = 0L,
    val audioFilePath: String? = null
)
