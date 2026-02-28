package com.flashback.app.data

import com.flashback.app.trigger.TriggerEvent
import com.flashback.app.trigger.TriggerHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** 觸發歷史記錄存取（記憶體內，未來可擴充至 Room） */
class TriggerHistoryRepository {

    private val history = TriggerHistory()
    private val _events = MutableStateFlow<List<TriggerEvent>>(emptyList())
    val events: StateFlow<List<TriggerEvent>> = _events.asStateFlow()

    fun add(event: TriggerEvent) {
        history.add(event)
        _events.value = history.events
    }

    fun clear() {
        history.clear()
        _events.value = emptyList()
    }

    val size: Int get() = history.size
}
