package com.flashback.app.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FftCalculatorTest {

    @Test
    fun `silence input returns all zero magnitudes`() {
        val silence = ShortArray(1024) { 0 }
        val magnitudes = FftCalculator.computeMagnitudes(silence)
        assertEquals(512, magnitudes.size)
        for (mag in magnitudes) {
            assertEquals(0f, mag, 0.001f)
        }
    }

    @Test
    fun `pure sine wave peaks at correct frequency bin`() {
        val sampleRate = 16000
        val freq = 1000.0  // 1kHz
        val n = 1024
        val samples = ShortArray(n) { i ->
            (16384 * kotlin.math.sin(2.0 * Math.PI * freq * i / sampleRate)).toInt().toShort()
        }

        val magnitudes = FftCalculator.computeMagnitudes(samples)

        // 1kHz bin index = freq * N / sampleRate = 1000 * 1024 / 16000 = 64
        val expectedBin = 64
        val peakBin = magnitudes.indices.maxByOrNull { magnitudes[it] } ?: -1

        assertTrue(
            "Peak bin ($peakBin) should be near expected bin ($expectedBin)",
            kotlin.math.abs(peakBin - expectedBin) <= 2
        )
    }

    @Test
    fun `different frequencies have different peak positions`() {
        val sampleRate = 16000
        val n = 1024

        val freq1 = 500.0
        val samples1 = ShortArray(n) { i ->
            (16384 * kotlin.math.sin(2.0 * Math.PI * freq1 * i / sampleRate)).toInt().toShort()
        }
        val mag1 = FftCalculator.computeMagnitudes(samples1)
        val peak1 = mag1.indices.maxByOrNull { mag1[it] } ?: -1

        val freq2 = 2000.0
        val samples2 = ShortArray(n) { i ->
            (16384 * kotlin.math.sin(2.0 * Math.PI * freq2 * i / sampleRate)).toInt().toShort()
        }
        val mag2 = FftCalculator.computeMagnitudes(samples2)
        val peak2 = mag2.indices.maxByOrNull { mag2[it] } ?: -1

        assertTrue("2kHz peak ($peak2) should be greater than 500Hz peak ($peak1)", peak2 > peak1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `non power of two length throws exception`() {
        FftCalculator.computeMagnitudes(ShortArray(100))
    }

    @Test
    fun `output length is half of input`() {
        val magnitudes = FftCalculator.computeMagnitudes(ShortArray(256) { 0 })
        assertEquals(128, magnitudes.size)
    }
}
