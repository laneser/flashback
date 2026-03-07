package com.flashback.app.audio

/**
 * Thread-safe 環形緩衝區，用於保留前 N 個 PCM 樣本（pre-trigger 音訊）。
 */
class PcmRingBuffer(private val capacity: Int) {

    private val buffer = ShortArray(capacity)
    private var writePos = 0
    private var count = 0

    /** 寫入 PCM 樣本到環形緩衝區 */
    @Synchronized
    fun write(samples: ShortArray) {
        for (sample in samples) {
            buffer[writePos] = sample
            writePos = (writePos + 1) % capacity
            if (count < capacity) count++
        }
    }

    /** 取得目前緩衝區內容的快照（按時間順序） */
    @Synchronized
    fun snapshot(): ShortArray {
        if (count == 0) return ShortArray(0)
        val result = ShortArray(count)
        if (count < capacity) {
            // 尚未填滿，資料從 0 開始
            buffer.copyInto(result, 0, 0, count)
        } else {
            // 已填滿，writePos 指向最舊的資料
            val firstLen = capacity - writePos
            buffer.copyInto(result, 0, writePos, writePos + firstLen)
            buffer.copyInto(result, firstLen, 0, writePos)
        }
        return result
    }

    /** 清除緩衝區 */
    @Synchronized
    fun clear() {
        writePos = 0
        count = 0
    }

    /** 目前緩衝區中的樣本數 */
    val size: Int get() = count
}
