package com.lottttto.miner.viewmodels

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lottttto.miner.models.CoinType
import com.lottttto.miner.models.Wallet
import com.lottttto.miner.repositories.WalletRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepositoryImpl,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            walletRepository.getAllWallets().collect { wallets ->
                _uiState.value = _uiState.value.copy(wallets = wallets)
            }
        }
    }

    fun addWallet(address: String, coin: CoinType, label: String?, seedPhrase: String?) {
        viewModelScope.launch { walletRepository.addWallet(address, coin, label, seedPhrase) }
    }

    fun updateWallet(wallet: Wallet) {
        viewModelScope.launch { walletRepository.updateWallet(wallet) }
    }

    fun deleteWallet(id: Long) {
        viewModelScope.launch { walletRepository.deleteWallet(id) }
    }

    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("wallet_data", text))
    }
}

data class WalletUiState(
    val wallets: List<Wallet> = emptyList()
)
