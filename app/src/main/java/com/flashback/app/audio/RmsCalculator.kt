package com.flashback.app.audio

import kotlin.math.log10
import kotlin.math.sqrt

/** RMS（均方根）音量計算，純函式無 Android 依賴 */
object RmsCalculator {

    /** 16-bit PCM 的最大值 */
    private const val MAX_16BIT = 32768.0

    /**
     * 計算 PCM 音訊資料的 RMS 分貝值。
     *
     * @param samples 16-bit PCM 音訊樣本
     * @return RMS dB 值（0~96 dB 範圍），靜音回傳 0.0
     */
    fun calculateDb(samples: ShortArray): Double {
        if (samples.isEmpty()) return 0.0

        var sumSquares = 0.0
        for (sample in samples) {
            val normalized = sample.toDouble() / MAX_16BIT
            sumSquares += normalized * normalized
        }

        val rms = sqrt(sumSquares / samples.size)
        if (rms < 1e-10) return 0.0

        // 轉換為 dB（以滿振幅為 96 dB 參考）
        val db = 20 * log10(rms) + 96.0
        return db.coerceAtLeast(0.0)
    }
}
