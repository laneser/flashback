package com.flashback.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flashback.app.data.SettingsRepository
import com.flashback.app.data.UserSettings
import com.flashback.app.flash.FlashControllerFactory
import com.flashback.app.flash.UsbSerialFlashController
import com.flashback.app.model.FlashMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)

    val settings: StateFlow<UserSettings> = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    fun updateVolumeThreshold(value: Double) {
        updateSettings { it.copy(volumeThresholdDb = value) }
    }

    fun updateClassificationConfidence(value: Float) {
        updateSettings { it.copy(classificationConfidence = value) }
    }

    fun updateMinDuration(value: Long) {
        updateSettings { it.copy(minDurationMs = value) }
    }

    fun updateStartHour(value: Int) {
        updateSettings { it.copy(startHour = value) }
    }

    fun updateEndHour(value: Int) {
        updateSettings { it.copy(endHour = value) }
    }

    fun updateClassificationEnabled(value: Boolean) {
        updateSettings { it.copy(classificationEnabled = value) }
    }

    fun updateTimeWindowEnabled(value: Boolean) {
        updateSettings { it.copy(timeWindowEnabled = value) }
    }

    fun updateFlashEnabled(value: Boolean) {
        updateSettings { it.copy(flashEnabled = value) }
    }

    fun updateFlashMode(value: FlashMode) {
        updateSettings { it.copy(flashMode = value) }
    }

    fun updateUsbBaudRate(value: Int) {
        updateSettings { it.copy(usbBaudRate = value) }
    }

    fun updateUsbDeviceIndex(value: Int) {
        updateSettings { it.copy(usbDeviceIndex = value) }
    }

    fun updateFlashDuration(value: Long) {
        updateSettings { it.copy(flashDurationMs = value) }
    }

    fun updateFlashInterval(value: Long) {
        updateSettings { it.copy(flashIntervalMs = value) }
    }

    fun updateFlashCount(value: Int) {
        updateSettings { it.copy(flashCount = value) }
    }

    fun updateTargetLabels(value: Set<String>) {
        updateSettings { it.copy(targetLabels = value) }
    }

    fun updateCooldown(value: Long) {
        updateSettings { it.copy(cooldownMs = value) }
    }

    /** 測試閃光：根據目前設定觸發一次閃光序列 */
    fun testFlash() {
        viewModelScope.launch {
            val current = settings.value
            val controller = FlashControllerFactory.create(getApplication(), current)
            try {
                controller.flashBurst()
            } finally {
                (controller as? UsbSerialFlashController)?.close()
            }
        }
    }

    private fun updateSettings(transform: (UserSettings) -> UserSettings) {
        viewModelScope.launch {
            val current = settings.value
            repository.updateSettings(transform(current))
        }
    }
}
