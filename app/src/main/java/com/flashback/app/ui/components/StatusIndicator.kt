package com.flashback.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flashback.app.model.MonitoringState

/** 監聽狀態指示器 */
@Composable
fun StatusIndicator(
    state: MonitoringState,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (state) {
        MonitoringState.IDLE -> Color.Gray to "閒置"
        MonitoringState.LISTENING -> Color(0xFF66BB6A) to "監聽中"
        MonitoringState.TRIGGERED -> Color(0xFFEF5350) to "已觸發！"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
