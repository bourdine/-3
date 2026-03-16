package com.lottttto.miner.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lottttto.miner.components.VerticalSlider
import com.lottttto.miner.viewmodels.MoneroMiningSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneroMiningSettingsScreen(
    onBack: () -> Unit,
    viewModel: MoneroMiningSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedPoolExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("⚙️ Настройки майнинга Monero", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 8.dp))
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⚡ Режим майнинга", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = uiState.miningMode == "pool", onClick = { viewModel.setMiningMode("pool") })
                        Text("Только пул", modifier = Modifier.padding(start = 8.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = uiState.miningMode == "solo", onClick = { viewModel.setMiningMode("solo") })
                        Text("Только соло", modifier = Modifier.padding(start = 8.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = uiState.miningMode == "both", onClick = { viewModel.setMiningMode("both") })
                        Text("Пул + соло (50%/50%)", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🌐 Выбор пула", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = selectedPoolExpanded,
                        onExpandedChange = { selectedPoolExpanded = it }
                    ) {
                        TextField(
                            value = uiState.selectedPool,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = selectedPoolExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = selectedPoolExpanded,
                            onDismissRequest = { selectedPoolExpanded = false }
                        ) {
                            listOf("MoneroOcean", "SupportXMR", "C3Pool").forEach { pool ->
                                DropdownMenuItem(
                                    text = { Text(pool) },
                                    onClick = { viewModel.setSelectedPool(pool); selectedPoolExpanded = false }
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🎚️ Распределение мощности", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Общая мощность: ${uiState.totalPower}%")
                    Slider(value = uiState.totalPower.toFloat(), onValueChange = { viewModel.setTotalPower(it.toInt()) }, valueRange = 0f..100f)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Пул: ${uiState.poolPercent}%")
                    VerticalSlider(value = uiState.poolPercent, onValueChange = { viewModel.setPoolPercent(it) }, modifier = Modifier.height(100.dp).width(40.dp))
                    Text("Соло: ${uiState.soloPercent}%")
                    VerticalSlider(value = uiState.soloPercent, onValueChange = { viewModel.setSoloPercent(it) }, modifier = Modifier.height(100.dp).width(40.dp))
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🔋 Условия майнинга", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = uiState.onlyWhenCharging, onCheckedChange = { viewModel.setOnlyWhenCharging(it) })
                        Text("Только при зарядке")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = uiState.onlyAtNight, onCheckedChange = { viewModel.setOnlyAtNight(it) })
                        Text("Только ночью (00:00 - 06:00)")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = uiState.onlyOnWiFi, onCheckedChange = { viewModel.setOnlyOnWiFi(it) })
                        Text("Только по Wi-Fi")
                    }
                    Text("Минимальный заряд: ${uiState.minBatteryLevel}%")
                    Slider(value = uiState.minBatteryLevel.toFloat(), onValueChange = { viewModel.setMinBatteryLevel(it.toInt()) }, valueRange = 0f..100f)
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🛡️ Защита устройства", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = uiState.stopOnLowBattery, onCheckedChange = { viewModel.setStopOnLowBattery(it) })
                        Text("Остановить при низком заряде (15%)")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = uiState.stopOnOverheat, onCheckedChange = { viewModel.setStopOnOverheat(it) })
                        Text("Остановить при перегреве (35°C)")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = uiState.autoStartOnLaunch, onCheckedChange = { viewModel.setAutoStartOnLaunch(it) })
                        Text("Автозапуск при старте приложения")
                    }
                }
            }
        }
        item {
            Button(onClick = { viewModel.saveSettings() }, modifier = Modifier.fillMaxWidth()) { Text("Сохранить настройки") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) { Text("Назад") }
        }
    }
}
