package com.flashback.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("設定") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 音量閾值
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("音量閾值", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "%.0f dB".format(settings.volumeThresholdDb),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = settings.volumeThresholdDb.toFloat(),
                        onValueChange = { viewModel.updateVolumeThreshold(it.toDouble()) },
                        valueRange = 40f..96f,
                        steps = 55
                    )
                }
            }

            // AI 分類信心度
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("AI 分類", style = MaterialTheme.typography.titleMedium)
                        Switch(
                            checked = settings.classificationEnabled,
                            onCheckedChange = { viewModel.updateClassificationEnabled(it) }
                        )
                    }
                    if (settings.classificationEnabled) {
                        Text(
                            "信心度閾值: %.0f%%".format(settings.classificationConfidence * 100),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Slider(
                            value = settings.classificationConfidence,
                            onValueChange = { viewModel.updateClassificationConfidence(it) },
                            valueRange = 0.3f..0.95f
                        )
                    }
                }
            }

            // 最小持續時間
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("最小持續時間", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${settings.minDurationMs} ms",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = settings.minDurationMs.toFloat(),
                        onValueChange = { viewModel.updateMinDuration(it.toLong()) },
                        valueRange = 100f..2000f,
                        steps = 18
                    )
                }
            }

            // 監聽時段
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("限定時段", style = MaterialTheme.typography.titleMedium)
                        Switch(
                            checked = settings.timeWindowEnabled,
                            onCheckedChange = { viewModel.updateTimeWindowEnabled(it) }
                        )
                    }
                    if (settings.timeWindowEnabled) {
                        Text(
                            "開始: %02d:00".format(settings.startHour),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Slider(
                            value = settings.startHour.toFloat(),
                            onValueChange = { viewModel.updateStartHour(it.toInt()) },
                            valueRange = 0f..23f,
                            steps = 22
                        )
                        Text(
                            "結束: %02d:00".format(settings.endHour),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Slider(
                            value = settings.endHour.toFloat(),
                            onValueChange = { viewModel.updateEndHour(it.toInt()) },
                            valueRange = 0f..23f,
                            steps = 22
                        )
                    }
                }
            }

            // 閃光燈
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("啟用閃光燈", style = MaterialTheme.typography.titleMedium)
                    Switch(
                        checked = settings.flashEnabled,
                        onCheckedChange = { viewModel.updateFlashEnabled(it) }
                    )
                }
            }
        }
    }
}
