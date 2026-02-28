package com.flashback.app.audio

import app.cash.turbine.test
import com.flashback.app.ml.ClassificationResult
import com.flashback.app.ml.SoundClassifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AudioPipelineTest {

    private class FakeAudioSource(private val frames: List<ShortArray>) : AudioCaptureSource {
        override fun capture(): Flow<ShortArray> = flowOf(*frames.toTypedArray())
        override fun stop() {}
    }

    private class FakeClassifier(
        private val result: ClassificationResult
    ) : SoundClassifier {
        override fun classify(samples: ShortArray): ClassificationResult = result
        override fun close() {}
    }

    @Test
    fun `pipeline computes RMS correctly`() = runTest {
        val samples = ShortArray(1024) { 16384 }
        val source = FakeAudioSource(listOf(samples))
        val pipeline = AudioPipeline(source)

        pipeline.start().test {
            val frame = awaitItem()
            assertTrue("RMS dB should be > 0", frame.rmsDb > 0.0)
            awaitComplete()
        }
    }

    @Test
    fun `pipeline computes FFT correctly`() = runTest {
        val samples = ShortArray(1024) { i ->
            (16384 * kotlin.math.sin(2.0 * Math.PI * 1000.0 * i / 16000)).toInt().toShort()
        }
        val source = FakeAudioSource(listOf(samples))
        val pipeline = AudioPipeline(source)

        pipeline.start().test {
            val frame = awaitItem()
            assertEquals(512, frame.fftMagnitudes.size)
            awaitComplete()
        }
    }

    @Test
    fun `no classifier means lastClassification is null`() = runTest {
        val source = FakeAudioSource(listOf(ShortArray(1024) { 0 }))
        val pipeline = AudioPipeline(source, classifier = null)

        pipeline.start().test {
            awaitItem()
            assertNull(pipeline.lastClassification)
            awaitComplete()
        }
    }

    @Test
    fun `silence input gives 0 RMS`() = runTest {
        val silence = ShortArray(1024) { 0 }
        val source = FakeAudioSource(listOf(silence))
        val pipeline = AudioPipeline(source)

        pipeline.start().test {
            val frame = awaitItem()
            assertEquals(0.0, frame.rmsDb, 0.001)
            awaitComplete()
        }
    }
}
