package com.lottttto.miner.screens.wallet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lottttto.miner.models.Transaction
import com.lottttto.miner.models.Wallet
import com.lottttto.miner.viewmodels.WalletViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    onBack: () -> Unit,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingWallet by remember { mutableStateOf<Wallet?>(null) }
    var selectedWalletForDetails by remember { mutableStateOf<Wallet?>(null) }
    var showSendDialog by remember { mutableStateOf(false) }
    var showDeleteTransactionDialog by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var showSeed by remember { mutableStateOf(false) }

    // Моковые данные для транзакций
    val transactions = remember {
        listOf(
            Transaction("Today", "+0.002345 XMR", "10:45", true),
            Transaction("Today", "+0.001234 XMR", "08:15", true),
            Transaction("Yesterday", "+0.003456 XMR", "23:30", true),
            Transaction("Yesterday", "-0.005000 XMR", "15:20", false)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monero Wallet") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* menu */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Карточка кошелька
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Адрес с кнопкой копирования
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Address: 4A1zP1eP1eP1eP1eP1eP1eP1eP1eP...QG...",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { /* copy address */ },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Balance: 0.1234 XMR  ≈  $18.51",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { showSendDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send XMR  ➡️")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seed phrase секция
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (!showSeed) {
                        Button(
                            onClick = { showSeed = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Show seed phrase")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "⚠️  Your seed phrase is the KEY to your funds. Never share it with anyone. Store it securely offline.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text(
                            text = "Seed phrase",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "abandon apple banana cherry december energy freedom galaxy hammer invite jungle kingdom liberty magnet nothing oxygen perfect quantum random secret token universe victory winter",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { /* copy seed */ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Copy seed phrase")
                            }
                            Button(
                                onClick = { showSeed = false },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text("Hide")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "⚠️  WARNING: Never share your seed phrase!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Транзакции
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
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
                        Text(
                            "Recent transactions",
                            style = MaterialTheme.typography.titleMedium
                        )
                        TextButton(onClick = { /* clear all */ }) {
                            Text("Clear")
                        }
                    }

                    LazyColumn {
                        items(transactions.groupBy { it.date }.toList()) { (date, txList) ->
                            Text(
                                text = date,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            txList.forEach { tx ->
                                TransactionItem(
                                    transaction = tx,
                                    onDelete = {
                                        selectedTransaction = tx
                                        showDeleteTransactionDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Диалог отправки
    if (showSendDialog) {
        SendXmrDialog(
            onDismiss = { showSendDialog = false },
            onSend = { to, amount, fee ->
                // Логика отправки
                showSendDialog = false
            }
        )
    }

    // Диалог удаления транзакции
    if (showDeleteTransactionDialog && selectedTransaction != null) {
        DeleteTransactionDialog(
            transaction = selectedTransaction!!,
            onDismiss = { showDeleteTransactionDialog = false },
            onConfirm = {
                // Логика удаления
                showDeleteTransactionDialog = false
                selectedTransaction = null
            }
        )
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (transaction.isIncoming) "➕" else "➖",
                modifier = Modifier.padding(end = 8.dp)
            )
            Column {
                Text(
                    text = "${if (transaction.isIncoming) "Block reward" else "Sent to external"}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = transaction.amount,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (transaction.isIncoming) Color.Green else Color.Red
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = transaction.time,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(end = 8.dp)
            )
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun SendXmrDialog(
    onDismiss: () -> Unit,
    onSend: (String, Double, String) -> Unit
) {
    var recipient by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var feeLevel by remember { mutableStateOf("Standard") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Send Monero (XMR)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    label = { Text("Recipient address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount XMR") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Slow", "Standard", "Fast").forEach { level ->
                        FilterChip(
                            selected = feeLevel == level,
                            onClick = { feeLevel = level },
                            label = { Text(level) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Text(
                    text = "Fee: 0.000015 XMR",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Total: ${amount.toDoubleOrNull()?.plus(0.000015) ?: 0.000015} XMR",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSend(
                        recipient,
                        amount.toDoubleOrNull() ?: 0.0,
                        feeLevel
                    )
                }
            ) {
                Text("Send XMR")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DeleteTransactionDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete transaction") },
        text = {
            Column {
                Text("Are you sure you want to delete this transaction?")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(transaction.amount)
                        Text(transaction.time)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "This action cannot be undone. The transaction will be removed from your history, but your balance will not be affected.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
