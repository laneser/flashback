package com.flashback.app.audio

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PcmRingBufferTest {

    private lateinit var buffer: PcmRingBuffer

    @Before
    fun setUp() {
        buffer = PcmRingBuffer(capacity = 10)
    }

    @Test
    fun `空 buffer snapshot 回傳空陣列`() {
        val result = buffer.snapshot()
        assertEquals(0, result.size)
        assertEquals(0, buffer.size)
    }

    @Test
    fun `部分填充 snapshot 回傳正確資料`() {
        buffer.write(shortArrayOf(1, 2, 3))
        val result = buffer.snapshot()
        assertArrayEquals(shortArrayOf(1, 2, 3), result)
        assertEquals(3, buffer.size)
    }

    @Test
    fun `填滿 buffer snapshot 回傳完整資料`() {
        buffer.write(shortArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
        val result = buffer.snapshot()
        assertArrayEquals(shortArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), result)
        assertEquals(10, buffer.size)
    }

    @Test
    fun `環繞寫入後 snapshot 回傳正確時間順序`() {
        // 先填滿
        buffer.write(shortArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
        // 再寫入 3 個，覆蓋最舊的
        buffer.write(shortArrayOf(11, 12, 13))

        val result = buffer.snapshot()
        // 應為 4,5,6,7,8,9,10,11,12,13（最舊的 1,2,3 被覆蓋）
        assertArrayEquals(shortArrayOf(4, 5, 6, 7, 8, 9, 10, 11, 12, 13), result)
        assertEquals(10, buffer.size)
    }

    @Test
    fun `多次環繞後 snapshot 正確`() {
        // 寫入超過 2 倍容量
        buffer.write(shortArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
        buffer.write(shortArrayOf(11, 12, 13, 14, 15, 16, 17, 18, 19, 20))
        buffer.write(shortArrayOf(21, 22, 23))

        val result = buffer.snapshot()
        assertArrayEquals(shortArrayOf(14, 15, 16, 17, 18, 19, 20, 21, 22, 23), result)
    }

    @Test
    fun `clear 後 snapshot 回傳空陣列`() {
        buffer.write(shortArrayOf(1, 2, 3, 4, 5))
        buffer.clear()
        val result = buffer.snapshot()
        assertEquals(0, result.size)
        assertEquals(0, buffer.size)
    }

    @Test
    fun `clear 後可以繼續寫入`() {
        buffer.write(shortArrayOf(1, 2, 3))
        buffer.clear()
        buffer.write(shortArrayOf(10, 20))
        val result = buffer.snapshot()
        assertArrayEquals(shortArrayOf(10, 20), result)
        assertEquals(2, buffer.size)
    }

    @Test
    fun `分批寫入等同一次寫入`() {
        val buffer2 = PcmRingBuffer(capacity = 10)

        buffer.write(shortArrayOf(1, 2, 3))
        buffer.write(shortArrayOf(4, 5))

        buffer2.write(shortArrayOf(1, 2, 3, 4, 5))

        assertArrayEquals(buffer2.snapshot(), buffer.snapshot())
    }
}
