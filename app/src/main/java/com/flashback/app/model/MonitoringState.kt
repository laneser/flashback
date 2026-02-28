package com.flashback.app.model

/** 監聽服務的狀態 */
enum class MonitoringState {
    /** 閒置，未監聽 */
    IDLE,
    /** 正在監聽環境音 */
    LISTENING,
    /** 已觸發閃光嚇阻 */
    TRIGGERED
}
