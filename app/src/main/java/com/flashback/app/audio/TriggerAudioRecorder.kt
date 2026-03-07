package com.flashback.app.audio

import android.content.Context
import com.flashback.app.model.AppConstants
import com.flashback.app.model.AudioFrame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 觸發音訊錄製器。
 * 持續將音訊幀寫入環形緩衝區（pre-trigger），
 * 觸發時擷取前 2 秒 + 收集後 2 秒，合併後編碼為 MP3。
 */
class TriggerAudioRecorder(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val ringBuffer = PcmRingBuffer(AppConstants.RING_BUFFER_SAMPLES)

    // 後觸發收集狀態
    @Volatile
    private var isCollectingPost = false
    private val postBuffer = mutableListOf<Short>()
    private var preTriggerSnapshot: ShortArray? = null
    private var triggerTimestampMs: Long = 0L
    private var onCompleteCallback: ((String?) -> Unit)? = null

    /** 每幀音訊資料餵入（由 MonitoringService 呼叫） */
    fun onFrame(frame: AudioFrame) {
        // 永遠餵入環形緩衝區
        ringBuffer.write(frame.pcmData)

        // 若正在收集後觸發音訊
        if (isCollectingPost) {
            synchronized(postBuffer) {
                for (sample in frame.pcmData) {
                    postBuffer.add(sample)
                }
                if (postBuffer.size >= AppConstants.POST_TRIGGER_SAMPLES) {
                    finalizeRecording()
                }
            }
        }
    }

    /**
     * 觸發錄製。非阻塞，完成後透過 callback 回傳檔案路徑。
     * @param timestampMs 觸發時間戳
     * @param onComplete 完成回呼，參數為 MP3 檔案路徑（失敗時為 null）
     */
    fun onTrigger(timestampMs: Long, onComplete: (String?) -> Unit) {
        // 若已在錄製中，忽略
        if (isCollectingPost) {
            onComplete(null)
            return
        }

        triggerTimestampMs = timestampMs
        preTriggerSnapshot = ringBuffer.snapshot()
        onCompleteCallback = onComplete

        synchronized(postBuffer) {
            postBuffer.clear()
        }
        isCollectingPost = true
    }

    private fun finalizeRecording() {
        isCollectingPost = false

        val pre = preTriggerSnapshot ?: ShortArray(0)
        val post: ShortArray
        synchronized(postBuffer) {
            post = postBuffer.take(AppConstants.POST_TRIGGER_SAMPLES).toShortArray()
            postBuffer.clear()
        }
        preTriggerSnapshot = null

        val callback = onCompleteCallback
        onCompleteCallback = null

        // 組合 PCM 並在背景編碼
        scope.launch(Dispatchers.IO) {
            val combined = ShortArray(pre.size + post.size)
            pre.copyInto(combined, 0)
            post.copyInto(combined, pre.size)

            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US)
            val fileName = "trigger_${dateFormat.format(Date(triggerTimestampMs))}.mp3"
            val outputDir = context.getExternalFilesDir("trigger_audio")
            val outputFile = File(outputDir, fileName)

            val success = Mp3Encoder.encode(combined, outputFile)
            callback?.invoke(if (success) outputFile.absolutePath else null)
        }
    }

    /** 停止錄製並清理 */
    fun stop() {
        isCollectingPost = false
        synchronized(postBuffer) {
            postBuffer.clear()
        }
        preTriggerSnapshot = null
        onCompleteCallback = null
        ringBuffer.clear()
    }
}
