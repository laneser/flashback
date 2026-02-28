package com.flashback.app.audio

import com.flashback.app.ml.ClassificationResult
import com.flashback.app.ml.SoundClassifier
import com.flashback.app.model.AppConstants
import com.flashback.app.model.AudioFrame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 音訊處理管線：capture → RMS + FFT → 分類
 * 將原始 PCM 資料轉換為包含分析結果的 AudioFrame
 */
class AudioPipeline(
    private val captureSource: AudioCaptureSource,
    private val classifier: SoundClassifier? = null
) {
    /** 用於 YAMNet 推論的累積 buffer */
    private val classificationBuffer = mutableListOf<Short>()

    /** 最新的分類結果 */
    var lastClassification: ClassificationResult? = null
        private set

    /**
     * 啟動管線，回傳 AudioFrame Flow。
     * 每個 frame 包含 RMS dB 和 FFT 幅度。
     */
    fun start(): Flow<AudioFrame> {
        classificationBuffer.clear()
        lastClassification = null

        return captureSource.capture().map { pcmData ->
            val rmsDb = RmsCalculator.calculateDb(pcmData)

            val fftMagnitudes = if (pcmData.size >= 2 && isPowerOfTwo(pcmData.size)) {
                FftCalculator.computeMagnitudes(pcmData)
            } else if (pcmData.size >= AppConstants.FFT_SIZE) {
                // 取前 FFT_SIZE 個樣本
                FftCalculator.computeMagnitudes(pcmData.copyOf(AppConstants.FFT_SIZE))
            } else {
                // 不足 FFT_SIZE，用零填充到最近的 2 次冪
                val paddedSize = nextPowerOfTwo(pcmData.size)
                val padded = ShortArray(paddedSize)
                pcmData.copyInto(padded)
                FftCalculator.computeMagnitudes(padded)
            }

            // 累積樣本做 YAMNet 分類
            if (classifier != null) {
                for (sample in pcmData) {
                    classificationBuffer.add(sample)
                }
                if (classificationBuffer.size >= AppConstants.YAMNET_INPUT_SAMPLES) {
                    val samples = classificationBuffer
                        .take(AppConstants.YAMNET_INPUT_SAMPLES)
                        .toShortArray()
                    lastClassification = classifier.classify(samples)
                    classificationBuffer.clear()
                }
            }

            AudioFrame(
                pcmData = pcmData,
                rmsDb = rmsDb,
                fftMagnitudes = fftMagnitudes
            )
        }
    }

    fun stop() {
        captureSource.stop()
        classificationBuffer.clear()
    }

    private fun isPowerOfTwo(n: Int): Boolean = n > 0 && n and (n - 1) == 0

    private fun nextPowerOfTwo(n: Int): Int {
        var v = n - 1
        v = v or (v shr 1)
        v = v or (v shr 2)
        v = v or (v shr 4)
        v = v or (v shr 8)
        v = v or (v shr 16)
        return v + 1
    }
}
