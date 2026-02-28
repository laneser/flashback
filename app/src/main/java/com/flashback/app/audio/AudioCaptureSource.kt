package com.flashback.app.audio

import kotlinx.coroutines.flow.Flow

/** 音訊擷取來源介面，方便測試時替換為 mock */
interface AudioCaptureSource {
    /** 開始擷取音訊，回傳 PCM 樣本的 Flow */
    fun capture(): Flow<ShortArray>

    /** 停止擷取 */
    fun stop()
}
