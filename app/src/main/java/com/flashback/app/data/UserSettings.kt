package com.flashback.app.data

import com.flashback.app.model.AppConstants

/** 使用者設定 */
data class UserSettings(
    val volumeThresholdDb: Double = AppConstants.DEFAULT_VOLUME_THRESHOLD_DB,
    val classificationConfidence: Float = AppConstants.DEFAULT_CLASSIFICATION_CONFIDENCE,
    val minDurationMs: Long = AppConstants.DEFAULT_MIN_DURATION_MS,
    val startHour: Int = AppConstants.DEFAULT_START_HOUR,
    val endHour: Int = AppConstants.DEFAULT_END_HOUR,
    val classificationEnabled: Boolean = true,
    val timeWindowEnabled: Boolean = true,
    val flashEnabled: Boolean = true
)
