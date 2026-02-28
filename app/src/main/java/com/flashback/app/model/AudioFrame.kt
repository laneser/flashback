package com.flashback.app.model

/** 音訊幀資料，封裝一次擷取的 PCM 資料及其分析結果 */
data class AudioFrame(
    val pcmData: ShortArray,
    val rmsDb: Double = 0.0,
    val fftMagnitudes: FloatArray = floatArrayOf(),
    val timestampMs: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AudioFrame) return false
        return pcmData.contentEquals(other.pcmData) &&
            rmsDb == other.rmsDb &&
            fftMagnitudes.contentEquals(other.fftMagnitudes) &&
            timestampMs == other.timestampMs
    }

    override fun hashCode(): Int {
        var result = pcmData.contentHashCode()
        result = 31 * result + rmsDb.hashCode()
        result = 31 * result + fftMagnitudes.contentHashCode()
        result = 31 * result + timestampMs.hashCode()
        return result
    }
}
