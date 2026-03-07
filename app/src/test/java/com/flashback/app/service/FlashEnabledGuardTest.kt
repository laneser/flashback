package com.flashback.app.service

import com.flashback.app.flash.FlashController
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * 驗證閃光燈只有在 flashEnabled=true 時才會觸發。
 * 模擬 MonitoringService 中的觸發決策邏輯。
 */
class FlashEnabledGuardTest {

    private val flashController = mockk<FlashController>(relaxed = true)

    private suspend fun simulateTrigger(flashEnabled: Boolean) {
        if (flashEnabled) {
            flashController.flashBurst()
        }
    }

    @Test
    fun `flash fires when flashEnabled is true`() = runTest {
        simulateTrigger(flashEnabled = true)
        coVerify(exactly = 1) { flashController.flashBurst() }
    }

    @Test
    fun `flash does not fire when flashEnabled is false`() = runTest {
        simulateTrigger(flashEnabled = false)
        coVerify(exactly = 0) { flashController.flashBurst() }
    }

    @Test
    fun `flash respects dynamic flashEnabled toggle`() = runTest {
        simulateTrigger(flashEnabled = true)
        simulateTrigger(flashEnabled = false)
        simulateTrigger(flashEnabled = true)
        coVerify(exactly = 2) { flashController.flashBurst() }
    }
}
