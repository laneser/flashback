package com.flashback.app.ui.history

import androidx.lifecycle.ViewModel
import com.flashback.app.data.TriggerHistoryRepository
import com.flashback.app.trigger.TriggerEvent
import kotlinx.coroutines.flow.StateFlow

class HistoryViewModel(
    private val historyRepository: TriggerHistoryRepository
) : ViewModel() {

    val events: StateFlow<List<TriggerEvent>> = historyRepository.events

    fun clearHistory() {
        historyRepository.clear()
    }
}
