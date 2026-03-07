package com.flashback.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flashback.app.ml.YamNetLabels
import com.flashback.app.model.FlashMode

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
                        Column {
                            Text("AI 分類", style = MaterialTheme.typography.titleMedium)
                            if (!settings.classificationEnabled) {
                                Text(
                                    "關閉 = 任何分類都觸發",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
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
                        Spacer(modifier = Modifier.height(4.dp))
                        var showLabelPicker by remember { mutableStateOf(false) }
                        OutlinedButton(
                            onClick = { showLabelPicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("觸發分類 (已選 ${settings.targetLabels.size} 項)")
                        }
                        if (showLabelPicker) {
                            LabelPickerDialog(
                                selectedLabels = settings.targetLabels,
                                onConfirm = { selected ->
                                    viewModel.updateTargetLabels(selected)
                                    showLabelPicker = false
                                },
                                onDismiss = { showLabelPicker = false }
                            )
                        }
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

            // 觸發冷卻時間
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("觸發冷卻時間", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "%.1f 秒".format(settings.cooldownMs / 1000.0),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = settings.cooldownMs.toFloat(),
                        onValueChange = { viewModel.updateCooldown(it.toLong()) },
                        valueRange = 1000f..30000f,
                        steps = 28
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
                        Column {
                            Text("限定時段", style = MaterialTheme.typography.titleMedium)
                            if (!settings.timeWindowEnabled) {
                                Text(
                                    "關閉 = 全時段監聽",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("啟用閃光燈", style = MaterialTheme.typography.titleMedium)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (settings.flashEnabled) {
                                Button(onClick = { viewModel.testFlash() }) {
                                    Text("測試")
                                }
                            }
                            Switch(
                                checked = settings.flashEnabled,
                                onCheckedChange = { viewModel.updateFlashEnabled(it) }
                            )
                        }
                    }
                    if (settings.flashEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("閃光模式", style = MaterialTheme.typography.bodyMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = settings.flashMode == FlashMode.PHONE_FLASH,
                                onClick = { viewModel.updateFlashMode(FlashMode.PHONE_FLASH) }
                            )
                            Text("手機閃光燈")
                            Spacer(modifier = Modifier.width(16.dp))
                            RadioButton(
                                selected = settings.flashMode == FlashMode.USB_RELAY,
                                onClick = { viewModel.updateFlashMode(FlashMode.USB_RELAY) }
                            )
                            Text("USB 繼電器")
                        }
                        // 閃光參數
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "持續時間: ${settings.flashDurationMs} ms",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Slider(
                            value = settings.flashDurationMs.toFloat(),
                            onValueChange = { viewModel.updateFlashDuration(it.toLong()) },
                            valueRange = 50f..500f,
                            steps = 8
                        )
                        Text(
                            "間隔時間: ${settings.flashIntervalMs} ms",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Slider(
                            value = settings.flashIntervalMs.toFloat(),
                            onValueChange = { viewModel.updateFlashInterval(it.toLong()) },
                            valueRange = 50f..500f,
                            steps = 8
                        )
                        Text(
                            "閃光次數: ${settings.flashCount}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Slider(
                            value = settings.flashCount.toFloat(),
                            onValueChange = { viewModel.updateFlashCount(it.toInt()) },
                            valueRange = 1f..5f,
                            steps = 3
                        )
                        if (settings.flashMode == FlashMode.USB_RELAY) {
                            Spacer(modifier = Modifier.height(8.dp))
                            // 裝置編號
                            Text(
                                "裝置編號: ${settings.usbDeviceIndex}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Slider(
                                value = settings.usbDeviceIndex.toFloat(),
                                onValueChange = { viewModel.updateUsbDeviceIndex(it.toInt()) },
                                valueRange = 0f..3f,
                                steps = 2
                            )
                            // 鮑率
                            val baudRateOptions = listOf(4800, 9600, 19200, 38400, 57600, 115200)
                            var baudRateExpanded by remember { mutableStateOf(false) }
                            Text("鮑率", style = MaterialTheme.typography.bodyMedium)
                            ExposedDropdownMenuBox(
                                expanded = baudRateExpanded,
                                onExpandedChange = { baudRateExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = "${settings.usbBaudRate}",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = baudRateExpanded)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                )
                                ExposedDropdownMenu(
                                    expanded = baudRateExpanded,
                                    onDismissRequest = { baudRateExpanded = false }
                                ) {
                                    baudRateOptions.forEach { rate ->
                                        DropdownMenuItem(
                                            text = { Text("$rate") },
                                            onClick = {
                                                viewModel.updateUsbBaudRate(rate)
                                                baudRateExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** 標籤多選對話框，支援搜尋 */
@Composable
private fun LabelPickerDialog(
    selectedLabels: Set<String>,
    onConfirm: (Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(selectedLabels.toMutableSet()) }
    val allLabels = remember { YamNetLabels.LABELS.toList() }
    val filteredLabels by remember(searchQuery) {
        derivedStateOf {
            if (searchQuery.isBlank()) allLabels
            else allLabels.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("選擇觸發分類 (${selected.size} 項)") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("搜尋...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { selected = filteredLabels.toMutableSet() }) {
                        Text("全選")
                    }
                    TextButton(onClick = { selected = mutableSetOf() }) {
                        Text("清除")
                    }
                }
                LazyColumn(modifier = Modifier.height(400.dp)) {
                    items(filteredLabels) { label ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selected = selected.toMutableSet().apply {
                                        if (contains(label)) remove(label) else add(label)
                                    }
                                }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = label in selected,
                                onCheckedChange = {
                                    selected = selected.toMutableSet().apply {
                                        if (it) add(label) else remove(label)
                                    }
                                }
                            )
                            Text(label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selected) }) {
                Text("確認")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
