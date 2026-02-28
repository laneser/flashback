package com.flashback.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp

/** FFT 頻譜視覺化 */
@Composable
fun SpectrumView(
    magnitudes: FloatArray,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        // 背景
        drawRect(color = surfaceVariant)

        if (magnitudes.isEmpty()) return@Canvas

        val barCount = minOf(magnitudes.size, 64) // 最多顯示 64 條
        val step = magnitudes.size / barCount
        val barWidth = size.width / barCount
        val maxMag = magnitudes.max().coerceAtLeast(0.001f)

        for (i in 0 until barCount) {
            val idx = i * step
            val magnitude = magnitudes[idx]
            val normalizedHeight = (magnitude / maxMag) * size.height
            val barHeight = normalizedHeight.coerceAtLeast(1f)

            drawRect(
                color = primaryColor,
                topLeft = Offset(i * barWidth, size.height - barHeight),
                size = Size(barWidth * 0.8f, barHeight)
            )
        }
    }
}
