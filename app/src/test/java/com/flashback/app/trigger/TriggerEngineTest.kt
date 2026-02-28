package com.flashback.app.trigger

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class TriggerEngineTest {

    private lateinit var engine: TriggerEngine

    @Before
    fun setup() {
        engine = TriggerEngine(
            volumeThresholdDb = 70.0,
            classificationConfidence = 0.7f,
            minDurationMs = 300L,
            classificationEnabled = false,
            timeWindowEnabled = false
        )
    }

    @Test
    fun `insufficient volume does not trigger`() {
        val result = engine.evaluate(volumeDb = 50.0, currentTimeMs = 1000L)
        assertNull(result)
    }

    @Test
    fun `sufficient volume but short duration does not trigger`() {
        assertNull(engine.evaluate(volumeDb = 80.0, currentTimeMs = 1000L))
        assertNull(engine.evaluate(volumeDb = 80.0, currentTimeMs = 1100L))
    }

    @Test
    fun `sufficient volume with enough duration triggers`() {
        assertNull(engine.evaluate(volumeDb = 80.0, currentTimeMs = 1000L))
        assertNull(engine.evaluate(volumeDb = 80.0, currentTimeMs = 1200L))
        val result = engine.evaluate(volumeDb = 80.0, currentTimeMs = 1400L)
        assertNotNull("400ms sustained should trigger", result)
    }

    @Test
    fun `volume interruption resets timer`() {
        engine.evaluate(volumeDb = 80.0, currentTimeMs = 1000L)
        engine.evaluate(volumeDb = 80.0, currentTimeMs = 1200L)
        // Volume drops, resets timer
        engine.evaluate(volumeDb = 50.0, currentTimeMs = 1300L)
        // Restart accumulation
        assertNull(engine.evaluate(volumeDb = 80.0, currentTimeMs = 1400L))
        assertNull(engine.evaluate(volumeDb = 80.0, currentTimeMs = 1600L))
        val result = engine.evaluate(volumeDb = 80.0, currentTimeMs = 1800L)
        assertNotNull("Re-accumulated 400ms should trigger", result)
    }

    @Test
    fun `AND logic with classification enabled`() {
        engine.classificationEnabled = true
        engine.targetLabels = setOf("Vehicle")

        // Volume OK but classification wrong
        assertNull(engine.evaluate(
            volumeDb = 80.0,
            classificationLabel = "Speech",
            classificationConf = 0.9f,
            currentTimeMs = 1000L
        ))

        // Classification OK but volume too low
        engine.reset()
        assertNull(engine.evaluate(
            volumeDb = 50.0,
            classificationLabel = "Vehicle",
            classificationConf = 0.8f,
            currentTimeMs = 2000L
        ))

        // Both OK with enough duration
        engine.reset()
        engine.evaluate(
            volumeDb = 80.0,
            classificationLabel = "Vehicle",
            classificationConf = 0.8f,
            currentTimeMs = 3000L
        )
        val result = engine.evaluate(
            volumeDb = 80.0,
            classificationLabel = "Vehicle",
            classificationConf = 0.8f,
            currentTimeMs = 3400L
        )
        assertNotNull("Volume + classification + duration all met should trigger", result)
    }

    @Test
    fun `trigger resets to prevent consecutive triggers`() {
        assertNull(engine.evaluate(volumeDb = 80.0, currentTimeMs = 1000L))
        assertNotNull(engine.evaluate(volumeDb = 80.0, currentTimeMs = 1400L))
        // After trigger, needs to re-accumulate
        assertNull(engine.evaluate(volumeDb = 80.0, currentTimeMs = 1500L))
    }

    @Test
    fun `reset clears internal state`() {
        engine.evaluate(volumeDb = 80.0, currentTimeMs = 1000L)
        engine.evaluate(volumeDb = 80.0, currentTimeMs = 1200L)
        engine.reset()
        assertNull(engine.evaluate(volumeDb = 80.0, currentTimeMs = 1300L))
    }
}
