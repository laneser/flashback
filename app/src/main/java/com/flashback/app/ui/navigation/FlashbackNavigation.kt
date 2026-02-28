package com.flashback.app.ui.navigation

import kotlinx.serialization.Serializable

/** 導航路由定義 */
object FlashbackNavigation {
    @Serializable
    object Main

    @Serializable
    object Settings

    @Serializable
    object History
}
