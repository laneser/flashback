package com.flashback.app.ml

/** YAMNet 分類結果 */
data class ClassificationResult(
    val label: String,
    val confidence: Float,
    val topResults: List<Pair<String, Float>> = emptyList()
)
