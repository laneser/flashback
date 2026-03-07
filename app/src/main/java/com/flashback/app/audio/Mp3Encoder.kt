package com.flashback.app.audio

import com.flashback.app.model.AppConstants
import java.io.File
import java.io.FileOutputStream

/**
 * PCM → MP3 編碼器，包裝 AndroidLame (TAndroidLame)。
 * 16kHz mono, 128kbps。
 */
object Mp3Encoder {

    /**
     * 將 PCM 樣本編碼為 MP3 檔案。
     * @param pcmSamples 16-bit PCM 資料（mono, 16kHz）
     * @param outputFile 輸出 MP3 檔案
     * @return 是否成功
     */
    fun encode(pcmSamples: ShortArray, outputFile: File): Boolean {
        return try {
            val lameBuilder = com.naman14.androidlame.LameBuilder()
                .setInSampleRate(AppConstants.SAMPLE_RATE)
                .setOutChannels(1)
                .setOutBitrate(AppConstants.MP3_BITRATE)
                .setQuality(AppConstants.MP3_QUALITY)

            val androidLame = lameBuilder.build()

            // MP3 輸出 buffer（估算最大值：1.25 * samples + 7200）
            val mp3BufSize = (1.25 * pcmSamples.size + 7200).toInt()
            val mp3Buf = ByteArray(mp3BufSize)

            // 編碼（mono：左右聲道使用同一 buffer）
            val bytesEncoded = androidLame.encode(
                pcmSamples, pcmSamples, pcmSamples.size, mp3Buf
            )

            // Flush 剩餘資料
            val flushBuf = ByteArray(7200)
            val flushBytes = androidLame.flush(flushBuf)

            // 寫入檔案
            outputFile.parentFile?.mkdirs()
            FileOutputStream(outputFile).use { fos ->
                if (bytesEncoded > 0) {
                    fos.write(mp3Buf, 0, bytesEncoded)
                }
                if (flushBytes > 0) {
                    fos.write(flushBuf, 0, flushBytes)
                }
            }

            androidLame.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
