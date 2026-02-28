package com.flashback.app.ml

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class YamNetClassifierTest {

    @Test
    fun `YamNetLabels contains 521 labels`() {
        assertEquals(521, YamNetLabels.LABELS.size)
    }

    @Test
    fun `YamNetLabels first label is Speech`() {
        assertEquals("Speech", YamNetLabels.LABELS[0])
    }

    @Test
    fun `YamNetLabels contains vehicle-related labels`() {
        val labels = YamNetLabels.LABELS.toSet()
        assertTrue("Should contain Vehicle", labels.contains("Vehicle"))
        assertTrue("Should contain Car", labels.contains("Car"))
        assertTrue("Should contain Motorcycle", labels.contains("Motorcycle"))
    }

    @Test
    fun `ClassificationResult data is correct`() {
        val result = ClassificationResult(
            label = "Vehicle",
            confidence = 0.85f,
            topResults = listOf("Vehicle" to 0.85f, "Car" to 0.7f)
        )
        assertEquals("Vehicle", result.label)
        assertEquals(0.85f, result.confidence, 0.001f)
        assertEquals(2, result.topResults.size)
    }
}
