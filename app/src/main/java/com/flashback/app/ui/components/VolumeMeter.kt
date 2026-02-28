package com.flashback.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** 即時音量條 */
@Composable
fun VolumeMeter(
    volumeDb: Double,
    thresholdDb: Double,
    modifier: Modifier = Modifier,
    maxDb: Double = 96.0
) {
    val colorScheme = MaterialTheme.colorScheme

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
    ) {
        val barWidth = size.width
        val barHeight = size.height

        // 背景
        drawRoundRect(
            color = colorScheme.surfaceVariant,
            cornerRadius = CornerRadius(8f, 8f),
            size = Size(barWidth, barHeight)
        )

        // 音量條
        val ratio = (volumeDb / maxDb).coerceIn(0.0, 1.0).toFloat()
        val barColor = when {
            volumeDb >= thresholdDb -> Color(0xFFEF5350) // 紅色：超過閾值
            volumeDb >= thresholdDb * 0.8 -> Color(0xFFFFA726) // 橘色：接近閾值
            else -> Color(0xFF66BB6A) // 綠色：正常
        }
        drawRoundRect(
            color = barColor,
            cornerRadius = CornerRadius(8f, 8f),
            size = Size(barWidth * ratio, barHeight)
        )

        // 閾值標記線
        val thresholdX = ((thresholdDb / maxDb) * barWidth).toFloat()
        drawLine(
            color = Color.White,
            start = Offset(thresholdX, 0f),
            end = Offset(thresholdX, barHeight),
            strokeWidth = 2f
        )
    }
}
