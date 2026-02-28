package com.flashback.app.ui.settings

import com.flashback.app.data.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SettingsViewModelTest {

    @Test
    fun `UserSettings threshold range is correct`() {
        val settings = UserSettings(volumeThresholdDb = 40.0)
        assertEquals(40.0, settings.volumeThresholdDb, 0.001)
    }

    @Test
    fun `UserSettings confidence range is correct`() {
        val settings = UserSettings(classificationConfidence = 0.95f)
        assertEquals(0.95f, settings.classificationConfidence, 0.001f)
    }

    @Test
    fun `UserSettings features can be disabled`() {
        val settings = UserSettings(
            classificationEnabled = false,
            timeWindowEnabled = false,
            flashEnabled = false
        )
        assertFalse(settings.classificationEnabled)
        assertFalse(settings.timeWindowEnabled)
        assertFalse(settings.flashEnabled)
    }
}
