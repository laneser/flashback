package com.flashback.app.flash

import android.content.Context
import com.flashback.app.data.UserSettings
import com.flashback.app.model.FlashMode

/** 根據使用者設定建立對應的 FlashController */
object FlashControllerFactory {

    fun create(context: Context, settings: UserSettings): FlashController {
        return when (settings.flashMode) {
            FlashMode.PHONE_FLASH -> CameraManagerFlashController(
                context = context,
                flashDurationMs = settings.flashDurationMs,
                flashIntervalMs = settings.flashIntervalMs,
                flashCount = settings.flashCount
            )
            FlashMode.USB_RELAY -> UsbSerialFlashController(
                context = context,
                baudRate = settings.usbBaudRate,
                deviceIndex = settings.usbDeviceIndex,
                flashDurationMs = settings.flashDurationMs,
                flashIntervalMs = settings.flashIntervalMs,
                flashCount = settings.flashCount
            )
        }
    }
}
