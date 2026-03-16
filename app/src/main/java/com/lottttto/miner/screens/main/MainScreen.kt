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
import com.lottttto.miner.viewmodels.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun MainScreen(
    onNavigateToWallet: () -> Unit,
    onNavigateToMiningSettings: () -> Unit,
    onNavigateToMining: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onLogout: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val computingUsage by viewModel.computingUsage.collectAsStateWithLifecycle()
    val visibleTasks = remember { viewModel.visibleTasks }
    val wallets by viewModel.wallets.collectAsStateWithLifecycle()
    var menuExpanded by remember { mutableStateOf(false) }
    
    var showBlockNotification by remember { mutableStateOf(false) }
    var lastBlockAmount by remember { mutableStateOf(0.0) }
    var lastBlockXmr by remember { mutableStateOf(0.0) }
    
    var isMiningActive by remember { mutableStateOf(true) } // по умолчанию запущен
    
    LaunchedEffect(Unit) {
        delay(5000)
        lastBlockAmount = 0.35
        lastBlockXmr = 0.002345
        showBlockNotification = true
    }

    val moneroWallet = wallets.firstOrNull { it.coin == CoinType.MONERO }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Lottttto", style = MaterialTheme.typography.headlineLarge)
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("⚙️ Настройки майнинга Monero") },
                        onClick = {
                            menuExpanded = false
                            onNavigateToMiningSettings()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("👛 Кошелёк") },
                        onClick = {
                            menuExpanded = false
                            onNavigateToWallet()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("ℹ️ About") },
                        onClick = {
                            menuExpanded = false
                            onNavigateToAbout()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("🚪 Выход") },
                        onClick = {
                            menuExpanded = false
                            onLogout()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (showBlockNotification) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎉", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                        Column {
                            Text(
                                "Block found! Congratulations!",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "You mined %.6f XMR (%.2f)".format(lastBlockXmr, lastBlockAmount),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    IconButton(
                        onClick = { showBlockNotification = false },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (moneroWallet != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Monero Wallet", style = MaterialTheme.typography.titleMedium)
                        IconButton(onClick = { /* menu */ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                    }
                    Text(
                        text = "Address: ${moneroWallet.address.take(16)}...${moneroWallet.address.takeLast(8)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Balance: 0.1234 XMR  ≈  $18.51",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onNavigateToWallet,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Manage Wallet")
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("No Monero wallet yet")
                    Button(onClick = onNavigateToWallet) {
                        Text("Add")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Current mining session", style = MaterialTheme.typography.titleMedium)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                StatRow("Hashrate:", "1,250 H/s")
                StatRow("Accepted shares:", "12,345")
                StatRow("Rejected shares:", "123")
                StatRow("Blocks found:", "3 ${if (showBlockNotification) "(+1 new!)" else ""}", highlight = showBlockNotification)
                StatRow("Session earnings:", "%.6f XMR".format(lastBlockXmr * 3))
                StatRow("Total earnings:", "0.1567 XMR")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Computing usage · $computingUsage%", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = computingUsage.toFloat(),
            onValueChange = { viewModel.setComputingUsage(it.toInt()) },
            valueRange = 0f..100f,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Resource allocation", style = MaterialTheme.typography.titleMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            visibleTasks.forEachIndexed { index, task ->
                val realIndex = viewModel.getRealIndex(index)
                val percent = viewModel.getVisiblePercentage(index)
                val label = if (task.mode == MiningMode.POOL) "Pool" else "Solo"
                val activeText = if (task.mode == MiningMode.POOL) "MoneroOcean" else "Solo mode"

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(label, style = MaterialTheme.typography.labelLarge)
                    VerticalSlider(
                        value = task.weight,
                        onValueChange = { viewModel.updateTaskWeight(realIndex, it) },
                        modifier = Modifier.height(150.dp)
                    )
                    Text("%.0f%%".format(percent))
                    Text(activeText, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("System status", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                StatRow("Temperature:", "35°C")
                StatRow("Battery:", "78%")
                StatRow("Network:", "WiFi connected")
                StatRow("Uptime:", "2h 15m")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { isMiningActive = !isMiningActive },
                modifier = Modifier.weight(1f),
                enabled = !isMiningActive,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isMiningActive) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            ) {
                Text("Start mining")
            }
            Button(
                onClick = { isMiningActive = !isMiningActive },
                modifier = Modifier.weight(1f),
                enabled = isMiningActive,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMiningActive) MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            ) {
                Text("Stop mining")
            }
        }
        
        Text(
            text = if (isMiningActive) "✓ Mining is active" else "○ Mining is stopped",
            style = MaterialTheme.typography.bodySmall,
            color = if (isMiningActive) Color.Green else Color.Gray,
            modifier = Modifier.padding(top = 4.dp).align(Alignment.CenterHorizontally)
        )
    }
}
