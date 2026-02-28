package com.flashback.app.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.flashback.app.audio.AudioPipeline
import com.flashback.app.audio.AudioRecordSource
import com.flashback.app.flash.CameraManagerFlashController
import com.flashback.app.flash.FlashController
import com.flashback.app.ml.SoundClassifier
import com.flashback.app.ml.YamNetClassifier
import com.flashback.app.model.AppConstants
import com.flashback.app.model.MonitoringState
import com.flashback.app.trigger.TriggerEngine
import com.flashback.app.trigger.TriggerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** 前景服務，持續監聽環境音並執行觸發邏輯 */
class MonitoringService : Service() {

    companion object {
        private val _state = MutableStateFlow(MonitoringState.IDLE)
        val state: StateFlow<MonitoringState> = _state.asStateFlow()

        private val _currentDb = MutableStateFlow(0.0)
        val currentDb: StateFlow<Double> = _currentDb.asStateFlow()

        private val _currentFft = MutableStateFlow(floatArrayOf())
        val currentFft: StateFlow<FloatArray> = _currentFft.asStateFlow()

        private val _classificationLabel = MutableStateFlow("")
        val classificationLabel: StateFlow<String> = _classificationLabel.asStateFlow()

        private val _classificationConfidence = MutableStateFlow(0f)
        val classificationConfidence: StateFlow<Float> = _classificationConfidence.asStateFlow()

        private val _triggerEvents = MutableSharedFlow<TriggerEvent>(extraBufferCapacity = 10)
        val triggerEvents = _triggerEvents.asSharedFlow()
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var monitorJob: Job? = null
    private var pipeline: AudioPipeline? = null
    private var flashController: FlashController? = null
    private var classifier: SoundClassifier? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        MonitoringNotification.createChannel(this)
        flashController = CameraManagerFlashController(this)
        classifier = try {
            YamNetClassifier(this)
        } catch (_: Exception) {
            null
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = MonitoringNotification.build(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                AppConstants.NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE or
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
            )
        } else {
            startForeground(AppConstants.NOTIFICATION_ID, notification)
        }

        startMonitoring()
        return START_STICKY
    }

    private fun startMonitoring() {
        if (monitorJob?.isActive == true) return

        val audioSource = AudioRecordSource()
        val audioPipeline = AudioPipeline(audioSource, classifier)
        pipeline = audioPipeline
        val triggerEngine = TriggerEngine()

        _state.value = MonitoringState.LISTENING

        monitorJob = serviceScope.launch {
            audioPipeline.start().collect { frame ->
                _currentDb.value = frame.rmsDb
                _currentFft.value = frame.fftMagnitudes

                val classification = audioPipeline.lastClassification
                val label = classification?.label ?: ""
                val confidence = classification?.confidence ?: 0f
                _classificationLabel.value = label
                _classificationConfidence.value = confidence

                val event = triggerEngine.evaluate(
                    volumeDb = frame.rmsDb,
                    classificationLabel = label,
                    classificationConf = confidence,
                    currentTimeMs = frame.timestampMs
                )

                if (event != null) {
                    _state.value = MonitoringState.TRIGGERED
                    _triggerEvents.emit(event)
                    flashController?.flashBurst()
                    _state.value = MonitoringState.LISTENING
                }
            }
        }
    }

    override fun onDestroy() {
        monitorJob?.cancel()
        pipeline?.stop()
        classifier?.close()
        _state.value = MonitoringState.IDLE
        _currentDb.value = 0.0
        _currentFft.value = floatArrayOf()
        serviceScope.cancel()
        super.onDestroy()
    }
}
