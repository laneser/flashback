package com.flashback.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsRepositoryTest {

    @Test
    fun `UserSettings default values are correct`() {
        val settings = UserSettings()
        assertEquals(70.0, settings.volumeThresholdDb, 0.001)
        assertEquals(0.7f, settings.classificationConfidence, 0.001f)
        assertEquals(300L, settings.minDurationMs)
        assertEquals(22, settings.startHour)
        assertEquals(6, settings.endHour)
        assertTrue(settings.classificationEnabled)
        assertTrue(settings.timeWindowEnabled)
        assertTrue(settings.flashEnabled)
    }

    @Test
    fun `UserSettings copy modifies correctly`() {
        val original = UserSettings()
        val modified = original.copy(volumeThresholdDb = 80.0)
        assertEquals(80.0, modified.volumeThresholdDb, 0.001)
        assertEquals(original.classificationConfidence, modified.classificationConfidence, 0.001f)
    }
}
