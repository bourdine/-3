package com.lottttto.miner.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lottttto.miner.components.StatRow
import com.lottttto.miner.components.VerticalSlider
import com.lottttto.miner.models.CoinType
import com.lottttto.miner.models.MiningMode
import com.lottttto.miner.viewmodels.MainViewModel   // <-- исправлено
import kotlinx.coroutines.delay

// ... остальной код без изменений

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiningScreen(
    onBack: () -> Unit,
    viewModel: MiningViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coinType = CoinType.MONERO

    LaunchedEffect(Unit) {
        viewModel.loadWalletsForCoin(coinType)
        viewModel.loadPoolsForCoin(coinType)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Monero (XMR)",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Выбор кошелька
        ExposedDropdownMenuBox(
            expanded = uiState.walletDropdownExpanded,
            onExpandedChange = { viewModel.toggleWalletDropdown() }
        ) {
            TextField(
                value = uiState.selectedWallet?.let {
                    it.label ?: it.address.take(12) + "..."
                } ?: "Select Wallet",
                onValueChange = {},
                readOnly = true,
                label = { Text("Wallet") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.walletDropdownExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = uiState.walletDropdownExpanded,
                onDismissRequest = { viewModel.toggleWalletDropdown() }
            ) {
                uiState.walletsForCoin.forEach { wallet ->
                    DropdownMenuItem(
                        text = { Text(wallet.label ?: wallet.address, maxLines = 1) },
                        onClick = {
                            viewModel.selectWallet(wallet)
                            viewModel.toggleWalletDropdown()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Переключатель режимов
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            MiningMode.values().forEach { mode ->
                FilterChip(
                    selected = uiState.selectedMode == mode,
                    onClick = { viewModel.selectMode(mode) },
                    label = { Text(mode.name) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Выбор пула (только для Pool режима)
        if (uiState.selectedMode == MiningMode.POOL) {
            ExposedDropdownMenuBox(
                expanded = uiState.poolDropdownExpanded,
                onExpandedChange = { viewModel.togglePoolDropdown() }
            ) {
                TextField(
                    value = uiState.selectedPool?.name ?: "Select Pool",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pool") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.poolDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = uiState.poolDropdownExpanded,
                    onDismissRequest = { viewModel.togglePoolDropdown() }
                ) {
                    uiState.availablePools.forEach { pool ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(pool.name)
                                    Text(pool.url, style = MaterialTheme.typography.bodySmall)
                                }
                            },
                            onClick = {
                                viewModel.selectPool(pool)
                                viewModel.togglePoolDropdown()
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Карточка со статистикой
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                StatRow("Hashrate", formatHashrate(uiState.hashrate))
                StatRow("Accepted", uiState.acceptedShares.toString())
                StatRow("Rejected", uiState.rejectedShares.toString())
                StatRow("Blocks Found", "3")
                StatRow("Est. earnings", "%.8f XMR".format(uiState.estimatedEarnings))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Кнопка запуска/остановки
        Button(
            onClick = {
                if (uiState.isMining) {
                    viewModel.stopMining()
                } else {
                    if (uiState.selectedWallet == null) return@Button
                    val pool = if (uiState.selectedMode == MiningMode.POOL) uiState.selectedPool else null
                    viewModel.startMining(
                        coin = coinType,
                        mode = uiState.selectedMode,
                        pool = pool,
                        wallet = uiState.selectedWallet!!
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = uiState.selectedWallet != null && (uiState.selectedMode != MiningMode.POOL || uiState.selectedPool != null),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (uiState.isMining) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(if (uiState.isMining) "Stop mining" else "Start mining")
        }
    }
}

private fun formatHashrate(hashrate: Double): String {
    return when {
        hashrate >= 1_000_000 -> "${DecimalFormat("#.##").format(hashrate / 1_000_000)} MH/s"
        hashrate >= 1_000 -> "${DecimalFormat("#.##").format(hashrate / 1_000)} KH/s"
        else -> "${DecimalFormat("#.##").format(hashrate)} H/s"
    }
}
