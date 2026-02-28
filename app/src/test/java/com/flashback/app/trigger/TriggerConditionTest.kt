package com.flashback.app.trigger

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TriggerConditionTest {

    @Test
    fun `VolumeCondition met when above threshold`() {
        assertTrue(VolumeCondition(80.0, 70.0).isMet())
    }

    @Test
    fun `VolumeCondition met when equal to threshold`() {
        assertTrue(VolumeCondition(70.0, 70.0).isMet())
    }

    @Test
    fun `VolumeCondition not met when below threshold`() {
        assertFalse(VolumeCondition(60.0, 70.0).isMet())
    }

    @Test
    fun `ClassificationCondition met with target label and sufficient confidence`() {
        val cond = ClassificationCondition(
            label = "Vehicle",
            confidence = 0.8f,
            requiredConfidence = 0.7f,
            targetLabels = setOf("Vehicle", "Motorcycle")
        )
        assertTrue(cond.isMet())
    }

    @Test
    fun `ClassificationCondition not met with non-target label`() {
        val cond = ClassificationCondition(
            label = "Speech",
            confidence = 0.9f,
            requiredConfidence = 0.7f,
            targetLabels = setOf("Vehicle", "Motorcycle")
        )
        assertFalse(cond.isMet())
    }

    @Test
    fun `ClassificationCondition not met with low confidence`() {
        val cond = ClassificationCondition(
            label = "Vehicle",
            confidence = 0.5f,
            requiredConfidence = 0.7f,
            targetLabels = setOf("Vehicle")
        )
        assertFalse(cond.isMet())
    }

    @Test
    fun `DurationCondition met when exceeds minimum`() {
        assertTrue(DurationCondition(500L, 300L).isMet())
    }

    @Test
    fun `DurationCondition not met when below minimum`() {
        assertFalse(DurationCondition(100L, 300L).isMet())
    }

    @Test
    fun `TimeWindowCondition overnight - within at 23h`() {
        // 22:00 - 06:00, current 23:00
        assertTrue(TimeWindowCondition(23, 22, 6).isMet())
    }

    @Test
    fun `TimeWindowCondition overnight - within at 2am`() {
        // 22:00 - 06:00, current 02:00
        assertTrue(TimeWindowCondition(2, 22, 6).isMet())
    }

    @Test
    fun `TimeWindowCondition overnight - outside at noon`() {
        // 22:00 - 06:00, current 12:00
        assertFalse(TimeWindowCondition(12, 22, 6).isMet())
    }

    @Test
    fun `TimeWindowCondition daytime - within at noon`() {
        // 08:00 - 22:00, current 12:00
        assertTrue(TimeWindowCondition(12, 8, 22).isMet())
    }

    @Test
    fun `TimeWindowCondition daytime - outside at 23h`() {
        // 08:00 - 22:00, current 23:00
        assertFalse(TimeWindowCondition(23, 8, 22).isMet())
    }
}
