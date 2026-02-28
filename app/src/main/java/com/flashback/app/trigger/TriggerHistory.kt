package com.flashback.app.trigger

/** 記憶體內觸發歷史記錄 */
class TriggerHistory(private val maxSize: Int = 100) {

    private val _events = mutableListOf<TriggerEvent>()

    /** 所有觸發事件（最新在前） */
    val events: List<TriggerEvent> get() = _events.toList()

    /** 新增觸發事件 */
    fun add(event: TriggerEvent) {
        _events.add(0, event)
        if (_events.size > maxSize) {
            _events.removeAt(_events.lastIndex)
        }
    }

    /** 清除所有記錄 */
    fun clear() {
        _events.clear()
    }

    /** 記錄數量 */
    val size: Int get() = _events.size
}
