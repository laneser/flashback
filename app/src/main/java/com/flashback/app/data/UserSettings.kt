package com.flashback.app.data

import com.flashback.app.model.AppConstants
import com.flashback.app.model.FlashMode
import com.flashback.app.trigger.TriggerEngine

/** 使用者設定 */
data class UserSettings(
    val volumeThresholdDb: Double = AppConstants.DEFAULT_VOLUME_THRESHOLD_DB,
    val classificationConfidence: Float = AppConstants.DEFAULT_CLASSIFICATION_CONFIDENCE,
    val minDurationMs: Long = AppConstants.DEFAULT_MIN_DURATION_MS,
    val startHour: Int = AppConstants.DEFAULT_START_HOUR,
    val endHour: Int = AppConstants.DEFAULT_END_HOUR,
    val classificationEnabled: Boolean = true,
    val timeWindowEnabled: Boolean = true,
    val flashEnabled: Boolean = true,
    val flashMode: FlashMode = FlashMode.PHONE_FLASH,
    val usbBaudRate: Int = AppConstants.DEFAULT_USB_BAUD_RATE,
    val usbDeviceIndex: Int = AppConstants.DEFAULT_USB_DEVICE_INDEX,
    val flashDurationMs: Long = AppConstants.DEFAULT_FLASH_DURATION_MS,
    val flashIntervalMs: Long = AppConstants.DEFAULT_FLASH_INTERVAL_MS,
    val flashCount: Int = AppConstants.DEFAULT_FLASH_COUNT,
    val targetLabels: Set<String> = TriggerEngine.DEFAULT_TARGET_LABELS,
    val cooldownMs: Long = AppConstants.DEFAULT_COOLDOWN_MS
)
