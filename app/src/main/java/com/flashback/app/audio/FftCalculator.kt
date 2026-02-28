package com.flashback.app.audio

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

/**
 * Cooley-Tukey radix-2 FFT 實作，純函式無外部依賴。
 * 輸入長度必須為 2 的冪次。
 */
object FftCalculator {

    /**
     * 對 PCM 樣本執行 FFT，回傳頻率幅度陣列。
     *
     * @param samples 16-bit PCM 樣本（長度應為 2 的冪次）
     * @return 頻率幅度陣列（長度 = samples.size / 2），已正規化
     */
    fun computeMagnitudes(samples: ShortArray): FloatArray {
        val n = samples.size
        require(n > 0 && n and (n - 1) == 0) { "FFT 輸入長度必須為 2 的冪次，取得: $n" }

        // 套用 Hann 窗並正規化至 [-1, 1]
        val real = DoubleArray(n)
        val imag = DoubleArray(n)
        for (i in 0 until n) {
            val window = 0.5 * (1.0 - cos(2.0 * PI * i / (n - 1)))
            real[i] = samples[i].toDouble() / 32768.0 * window
        }

        // Bit-reversal 排列
        bitReverse(real, imag)

        // Cooley-Tukey butterfly
        var len = 2
        while (len <= n) {
            val halfLen = len / 2
            val angle = -2.0 * PI / len
            for (i in 0 until n step len) {
                for (j in 0 until halfLen) {
                    val thetaVal = angle * j
                    val wr = cos(thetaVal)
                    val wi = kotlin.math.sin(thetaVal)
                    val tReal = wr * real[i + j + halfLen] - wi * imag[i + j + halfLen]
                    val tImag = wr * imag[i + j + halfLen] + wi * real[i + j + halfLen]
                    real[i + j + halfLen] = real[i + j] - tReal
                    imag[i + j + halfLen] = imag[i + j] - tImag
                    real[i + j] += tReal
                    imag[i + j] += tImag
                }
            }
            len *= 2
        }

        // 計算幅度（只取前半部，對稱）
        val halfN = n / 2
        val magnitudes = FloatArray(halfN)
        for (i in 0 until halfN) {
            magnitudes[i] = (sqrt(real[i] * real[i] + imag[i] * imag[i]) / halfN).toFloat()
        }
        return magnitudes
    }

    private fun bitReverse(real: DoubleArray, imag: DoubleArray) {
        val n = real.size
        var j = 0
        for (i in 1 until n) {
            var bit = n shr 1
            while (j and bit != 0) {
                j = j xor bit
                bit = bit shr 1
            }
            j = j xor bit
            if (i < j) {
                var temp = real[i]; real[i] = real[j]; real[j] = temp
                temp = imag[i]; imag[i] = imag[j]; imag[j] = temp
            }
        }
    }
}
