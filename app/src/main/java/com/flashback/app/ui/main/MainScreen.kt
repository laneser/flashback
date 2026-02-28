package com.flashback.app.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flashback.app.model.MonitoringState
import com.flashback.app.ui.components.PermissionHandler
import com.flashback.app.ui.components.SpectrumView
import com.flashback.app.ui.components.StatusIndicator
import com.flashback.app.ui.components.VolumeMeter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentDb by viewModel.currentDb.collectAsStateWithLifecycle()
    val currentFft by viewModel.currentFft.collectAsStateWithLifecycle()
    val classLabel by viewModel.classificationLabel.collectAsStateWithLifecycle()
    val classConf by viewModel.classificationConfidence.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val permissionsGranted by viewModel.permissionsGranted.collectAsStateWithLifecycle()

    PermissionHandler(
        onAllGranted = { viewModel.onPermissionsGranted() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flashback") },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "歷史記錄")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "設定")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 狀態指示器
            StatusIndicator(state = state)

            // 音量數值
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "即時音量",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "%.1f dB".format(currentDb),
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    VolumeMeter(
                        volumeDb = currentDb,
                        thresholdDb = settings.volumeThresholdDb
                    )
                }
            }

            // 頻譜圖
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "頻譜分析",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SpectrumView(magnitudes = currentFft)
                }
            }

            // AI 分類結果
            if (classLabel.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "AI 分類",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = classLabel,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "信心度: %.1f%%".format(classConf * 100),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 開始/停止按鈕
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                if (state == MonitoringState.IDLE) {
                    Button(
                        onClick = { viewModel.startMonitoring() },
                        enabled = permissionsGranted,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("開始監聽")
                    }
                } else {
                    Button(
                        onClick = { viewModel.stopMonitoring() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("停止監聽")
                    }
                }
            }
        }
    }
}
