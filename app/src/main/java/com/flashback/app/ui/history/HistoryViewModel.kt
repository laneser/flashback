package com.flashback.app.ui.history

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import com.flashback.app.data.TriggerHistoryRepository
import com.flashback.app.trigger.TriggerEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class HistoryViewModel(
    private val historyRepository: TriggerHistoryRepository
) : ViewModel() {

    val events: StateFlow<List<TriggerEvent>> = historyRepository.events

    private var mediaPlayer: MediaPlayer? = null

    private val _playingEventTimestamp = MutableStateFlow<Long?>(null)
    val playingEventTimestamp: StateFlow<Long?> = _playingEventTimestamp.asStateFlow()

    /** 切換播放/停止 */
    fun togglePlayback(event: TriggerEvent) {
        val path = event.audioFilePath ?: return

        if (_playingEventTimestamp.value == event.timestampMs) {
            stopPlayback()
            return
        }

        stopPlayback()

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(path)
                setOnCompletionListener { stopPlayback() }
                prepare()
                start()
            }
            _playingEventTimestamp.value = event.timestampMs
        } catch (e: Exception) {
            e.printStackTrace()
            stopPlayback()
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        _playingEventTimestamp.value = null
    }

    fun clearHistory() {
        stopPlayback()
        // 刪除音訊檔案
        historyRepository.events.value.forEach { event ->
            event.audioFilePath?.let { path ->
                try { File(path).delete() } catch (_: Exception) {}
            }
        }
        historyRepository.clear()
    }

    override fun onCleared() {
        stopPlayback()
        super.onCleared()
    }
}
