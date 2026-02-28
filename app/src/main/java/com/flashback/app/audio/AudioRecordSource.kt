package com.flashback.app.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.flashback.app.model.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

/** Android AudioRecord 封裝，實際從麥克風擷取 PCM 音訊 */
class AudioRecordSource : AudioCaptureSource {

    private var audioRecord: AudioRecord? = null

    @SuppressLint("MissingPermission")
    override fun capture(): Flow<ShortArray> = flow {
        val bufferSize = maxOf(
            AudioRecord.getMinBufferSize(
                AppConstants.SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ),
            AppConstants.AUDIO_BUFFER_SIZE * 2 // Short = 2 bytes
        )

        val record = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            AppConstants.SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioRecord = record

        try {
            record.startRecording()
            val buffer = ShortArray(AppConstants.AUDIO_BUFFER_SIZE)

            while (coroutineContext.isActive) {
                val read = record.read(buffer, 0, buffer.size)
                if (read > 0) {
                    emit(buffer.copyOf(read))
                }
            }
        } finally {
            record.stop()
            record.release()
            audioRecord = null
        }
    }.flowOn(Dispatchers.IO)

    override fun stop() {
        audioRecord?.stop()
    }
}
