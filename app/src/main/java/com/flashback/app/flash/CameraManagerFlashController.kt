package com.flashback.app.flash

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import com.flashback.app.model.AppConstants
import kotlinx.coroutines.delay

/**
 * 使用 Camera2 CameraManager.setTorchMode() 控制閃光燈。
 * 比 CameraX 更簡單直接，因為只需要開關手電筒。
 */
class CameraManagerFlashController(context: Context) : FlashController {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val cameraId: String? = findCameraWithFlash()

    private fun findCameraWithFlash(): String? {
        return try {
            cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        } catch (_: Exception) {
            null
        }
    }

    override fun turnOn() {
        cameraId?.let {
            try {
                cameraManager.setTorchMode(it, true)
            } catch (_: Exception) {
                // 閃光燈不可用時靜默失敗
            }
        }
    }

    override fun turnOff() {
        cameraId?.let {
            try {
                cameraManager.setTorchMode(it, false)
            } catch (_: Exception) {
                // 閃光燈不可用時靜默失敗
            }
        }
    }

    override suspend fun flashBurst() {
        repeat(AppConstants.FLASH_BURST_COUNT) {
            turnOn()
            delay(AppConstants.FLASH_DURATION_MS)
            turnOff()
            if (it < AppConstants.FLASH_BURST_COUNT - 1) {
                delay(AppConstants.FLASH_BURST_INTERVAL_MS)
            }
        }
    }

    override fun isAvailable(): Boolean = cameraId != null
}
