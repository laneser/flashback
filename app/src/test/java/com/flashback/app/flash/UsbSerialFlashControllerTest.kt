package com.flashback.app.flash

import com.flashback.app.model.AppConstants
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class UsbSerialFlashControllerTest {

    @Test
    fun `USB relay ON command bytes are correct`() {
        val expected = byteArrayOf(0xA0.toByte(), 0x01, 0x01, 0xA2.toByte())
        assertArrayEquals(expected, AppConstants.USB_RELAY_ON_COMMAND)
    }

    @Test
    fun `USB relay OFF command bytes are correct`() {
        val expected = byteArrayOf(0xA0.toByte(), 0x01, 0x00, 0xA1.toByte())
        assertArrayEquals(expected, AppConstants.USB_RELAY_OFF_COMMAND)
    }

    @Test
    fun `USB relay ON command has 4 bytes`() {
        assertEquals(4, AppConstants.USB_RELAY_ON_COMMAND.size)
    }

    @Test
    fun `USB relay OFF command has 4 bytes`() {
        assertEquals(4, AppConstants.USB_RELAY_OFF_COMMAND.size)
    }

    @Test
    fun `USB relay duration is 1000ms`() {
        assertEquals(1000L, AppConstants.USB_RELAY_DURATION_MS)
    }

    @Test
    fun `default USB baud rate is 9600`() {
        assertEquals(9600, AppConstants.DEFAULT_USB_BAUD_RATE)
    }

    @Test
    fun `default USB device index is 0`() {
        assertEquals(0, AppConstants.DEFAULT_USB_DEVICE_INDEX)
    }
}
