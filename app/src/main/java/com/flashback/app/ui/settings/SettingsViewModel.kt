package com.flashback.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flashback.app.data.SettingsRepository
import com.flashback.app.data.UserSettings
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

    private fun updateSettings(transform: (UserSettings) -> UserSettings) {
        viewModelScope.launch {
            val current = settings.value
            repository.updateSettings(transform(current))
        }
    }
}
