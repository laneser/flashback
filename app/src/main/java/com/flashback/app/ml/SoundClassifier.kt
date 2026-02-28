package com.flashback.app.ml

/** 聲音分類器介面 */
interface SoundClassifier {
    /**
     * 對 PCM 音訊樣本進行分類。
     *
     * @param samples 16-bit PCM 樣本（長度應為 YAMNET_INPUT_SAMPLES）
     * @return 分類結果
     */
    fun classify(samples: ShortArray): ClassificationResult

    /** 釋放資源 */
    fun close()
}
