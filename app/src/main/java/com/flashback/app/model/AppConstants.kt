package com.flashback.app.model

/** 全域常數定義 */
object AppConstants {
    // 音訊參數
    const val SAMPLE_RATE = 16000
    const val FFT_SIZE = 1024
    const val AUDIO_BUFFER_SIZE = 1024

    // YAMNet 參數
    const val YAMNET_INPUT_SAMPLES = 15600
    const val YAMNET_NUM_CLASSES = 521

    // 觸發預設值
    const val DEFAULT_VOLUME_THRESHOLD_DB = 70.0
    const val DEFAULT_CLASSIFICATION_CONFIDENCE = 0.7f
    const val DEFAULT_MIN_DURATION_MS = 300L

    // 閃光燈參數
    const val FLASH_DURATION_MS = 200L
    const val FLASH_BURST_COUNT = 3
    const val FLASH_BURST_INTERVAL_MS = 100L

    // USB 繼電器參數
    val USB_RELAY_ON_COMMAND = byteArrayOf(0xA0.toByte(), 0x01, 0x01, 0xA2.toByte())
    val USB_RELAY_OFF_COMMAND = byteArrayOf(0xA0.toByte(), 0x01, 0x00, 0xA1.toByte())
    const val USB_RELAY_DURATION_MS = 1000L
    const val DEFAULT_USB_BAUD_RATE = 9600
    const val DEFAULT_USB_DEVICE_INDEX = 0

    // Foreground Service 通知
    const val NOTIFICATION_CHANNEL_ID = "flashback_monitoring"
    const val NOTIFICATION_ID = 1

    // 預設監聽時段（24 小時制）
    const val DEFAULT_START_HOUR = 22
    const val DEFAULT_END_HOUR = 6
}
