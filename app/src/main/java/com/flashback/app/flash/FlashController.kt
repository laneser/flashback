package com.flashback.app.flash

/** 閃光燈控制介面 */
interface FlashController {
    /** 開啟閃光燈 */
    fun turnOn()

    /** 關閉閃光燈 */
    fun turnOff()

    /** 執行閃光嚇阻序列（快速閃爍多次） */
    suspend fun flashBurst()

    /** 是否可用 */
    fun isAvailable(): Boolean
}
