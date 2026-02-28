package com.flashback.app.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RmsCalculatorTest {

    @Test
    fun `empty array returns 0`() {
        assertEquals(0.0, RmsCalculator.calculateDb(shortArrayOf()), 0.001)
    }

    @Test
    fun `silence returns 0`() {
        val silence = ShortArray(1024) { 0 }
        assertEquals(0.0, RmsCalculator.calculateDb(silence), 0.001)
    }

    @Test
    fun `full scale returns near 96 dB`() {
        val fullScale = ShortArray(1024) { Short.MAX_VALUE }
        val db = RmsCalculator.calculateDb(fullScale)
        assertTrue("Full scale should be near 96 dB, got: $db", db > 95.0 && db <= 96.1)
    }

    @Test
    fun `known amplitude computes correctly`() {
        // Half amplitude: 16384/32768 = 0.5
        // RMS = 0.5, 20*log10(0.5) + 96 ~ 89.97 dB
        val halfScale = ShortArray(1024) { 16384 }
        val db = RmsCalculator.calculateDb(halfScale)
        assertEquals(89.97, db, 0.5)
    }

    @Test
    fun `sine wave computes reasonable dB`() {
        val sampleRate = 16000
        val freq = 440.0
        val samples = ShortArray(1024) { i ->
            (16384 * kotlin.math.sin(2.0 * Math.PI * freq * i / sampleRate)).toInt().toShort()
        }
        val db = RmsCalculator.calculateDb(samples)
        assertTrue("Sine wave dB should be reasonable, got: $db", db > 80.0 && db < 95.0)
    }

    @Test
    fun `negative samples handled correctly`() {
        val negative = ShortArray(1024) { Short.MIN_VALUE }
        val db = RmsCalculator.calculateDb(negative)
        assertTrue("Negative full scale should be near 96 dB, got: $db", db > 95.0)
    }
}
