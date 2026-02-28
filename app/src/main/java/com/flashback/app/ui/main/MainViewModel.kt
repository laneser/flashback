package com.flashback.app.ui.main

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flashback.app.data.SettingsRepository
import com.flashback.app.data.TriggerHistoryRepository
import com.flashback.app.data.UserSettings
import com.flashback.app.model.MonitoringState
import com.flashback.app.service.MonitoringService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)
    val historyRepository = TriggerHistoryRepository()

    val state: StateFlow<MonitoringState> = MonitoringService.state
    val currentDb: StateFlow<Double> = MonitoringService.currentDb
    val currentFft: StateFlow<FloatArray> = MonitoringService.currentFft
    val classificationLabel: StateFlow<String> = MonitoringService.classificationLabel
    val classificationConfidence: StateFlow<Float> = MonitoringService.classificationConfidence

    val settings: StateFlow<UserSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    private val _permissionsGranted = MutableStateFlow(false)
    val permissionsGranted: StateFlow<Boolean> = _permissionsGranted.asStateFlow()

    init {
        // 收集觸發事件並存入歷史
        viewModelScope.launch {
            MonitoringService.triggerEvents.collect { event ->
                historyRepository.add(event)
            }
        }
    }

    fun onPermissionsGranted() {
        _permissionsGranted.value = true
    }

    fun startMonitoring() {
        val context = getApplication<Application>()
        val intent = Intent(context, MonitoringService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopMonitoring() {
        val context = getApplication<Application>()
        context.stopService(Intent(context, MonitoringService::class.java))
    }
}
