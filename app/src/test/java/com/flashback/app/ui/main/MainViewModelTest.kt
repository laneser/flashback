package com.flashback.app.ui.main

import com.flashback.app.data.TriggerHistoryRepository
import com.flashback.app.trigger.TriggerEvent
import org.junit.Assert.assertEquals
import org.junit.Test

class MainViewModelTest {

    @Test
    fun `TriggerHistoryRepository adds events`() {
        val repo = TriggerHistoryRepository()
        val event = TriggerEvent(
            volumeDb = 80.0,
            classificationLabel = "Vehicle",
            classificationConfidence = 0.85f,
            durationMs = 500L
        )
        repo.add(event)
        assertEquals(1, repo.size)
        assertEquals("Vehicle", repo.events.value.first().classificationLabel)
    }

    @Test
    fun `TriggerHistoryRepository clears events`() {
        val repo = TriggerHistoryRepository()
        repo.add(TriggerEvent(volumeDb = 80.0))
        repo.add(TriggerEvent(volumeDb = 85.0))
        assertEquals(2, repo.size)
        repo.clear()
        assertEquals(0, repo.size)
    }

    @Test
    fun `TriggerHistoryRepository newest first`() {
        val repo = TriggerHistoryRepository()
        repo.add(TriggerEvent(volumeDb = 80.0, timestampMs = 1000L))
        repo.add(TriggerEvent(volumeDb = 85.0, timestampMs = 2000L))
        assertEquals(2000L, repo.events.value.first().timestampMs)
    }
}
