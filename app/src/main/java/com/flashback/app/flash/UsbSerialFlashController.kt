package com.flashback.app.flash

import android.content.Context
import android.hardware.usb.UsbManager
import com.flashback.app.model.AppConstants
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.delay

/**
 * 透過 USB-to-UART 繼電器模組控制外接閃光燈。
 * 發送特定位元組序列開關繼電器。
 */
class UsbSerialFlashController(
    private val context: Context,
    private val baudRate: Int = AppConstants.DEFAULT_USB_BAUD_RATE,
    private val deviceIndex: Int = AppConstants.DEFAULT_USB_DEVICE_INDEX
) : FlashController {

    private var port: UsbSerialPort? = null

    private fun ensureConnection() {
        if (port != null) return
        try {
            val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
            if (availableDrivers.isEmpty() || deviceIndex >= availableDrivers.size) return

            val driver = availableDrivers[deviceIndex]
            val connection = usbManager.openDevice(driver.device) ?: return
            val serialPort = driver.ports[0]
            serialPort.open(connection)
            serialPort.setParameters(
                baudRate,
                UsbSerialPort.DATABITS_8,
                UsbSerialPort.STOPBITS_1,
                UsbSerialPort.PARITY_NONE
            )
            port = serialPort
        } catch (_: Exception) {
            // USB 裝置不可用時靜默失敗
            port = null
        }
    }

    override fun turnOn() {
        try {
            ensureConnection()
            port?.write(AppConstants.USB_RELAY_ON_COMMAND, 1000)
        } catch (_: Exception) {
            // 靜默處理
        }
    }

    override fun turnOff() {
        try {
            ensureConnection()
            port?.write(AppConstants.USB_RELAY_OFF_COMMAND, 1000)
        } catch (_: Exception) {
            // 靜默處理
        }
    }

    override suspend fun flashBurst() {
        turnOn()
        delay(AppConstants.USB_RELAY_DURATION_MS)
        turnOff()
    }

    override fun isAvailable(): Boolean {
        return try {
            val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            val drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
            deviceIndex < drivers.size
        } catch (_: Exception) {
            false
        }
    }

    /** 釋放 USB 連線資源 */
    fun close() {
        try {
            port?.close()
        } catch (_: Exception) {
            // 靜默處理
        }
        port = null
    }
}
