package com.lottttto.miner.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lottttto.miner.models.*
import com.lottttto.miner.repositories.SettingsRepositoryImpl
import com.lottttto.miner.repositories.WalletRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepositoryImpl,
    private val walletRepository: WalletRepositoryImpl
) : ViewModel() {

    private val _tasks = MutableStateFlow(
        listOf(
            MiningTask(CoinType.MONERO, MiningMode.POOL),
            MiningTask(CoinType.MONERO, MiningMode.SOLO)
        ).toMutableList()
    )
    val tasks: StateFlow<List<MiningTask>> = _tasks.asStateFlow()

    private val visibleIndices = listOf(0, 1)
    val visibleTasks: List<MiningTask> get() = visibleIndices.map { _tasks.value[it] }

    fun getRealIndex(visiblePosition: Int): Int = visibleIndices[visiblePosition]

    private val _computingUsage = MutableStateFlow(15)
    val computingUsage: StateFlow<Int> = _computingUsage.asStateFlow()

    private val _displayPercentages = MutableStateFlow<List<Double>>(emptyList())
    val displayPercentages: StateFlow<List<Double>> = _displayPercentages.asStateFlow()

    private val _wallets = MutableStateFlow<List<Wallet>>(emptyList())
    val wallets: StateFlow<List<Wallet>> = _wallets.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getComputingUsage().collect { usage ->
                _computingUsage.value = usage
                recalcPercentages()
            }
        }
        viewModelScope.launch {
            settingsRepository.getTaskWeights().collect { weights ->
                if (weights.size == _tasks.value.size) {
                    _tasks.value = _tasks.value.mapIndexed { index, task -> task.copy(weight = weights[index]) }.toMutableList()
                    recalcPercentages()
                }
            }
        }
        viewModelScope.launch {
            walletRepository.getAllWallets().collect { wallets -> _wallets.value = wallets }
        }
    }

    fun updateTaskWeight(realIndex: Int, newWeight: Int) {
        if (realIndex !in _tasks.value.indices) return
        _tasks.value = _tasks.value.toMutableList().apply {
            this[realIndex] = this[realIndex].copy(weight = newWeight.coerceIn(0, 100))
        }
        recalcPercentages()
        viewModelScope.launch { settingsRepository.saveTaskWeights(_tasks.value.map { it.weight }) }
    }

    fun setComputingUsage(percent: Int) {
        _computingUsage.value = percent.coerceIn(0, 100)
        viewModelScope.launch { settingsRepository.saveComputingUsage(_computingUsage.value) }
        recalcPercentages()
    }

    private fun recalcPercentages() {
        val totalWeight = _tasks.value.sumOf { it.weight }.toDouble()
        val usage = _computingUsage.value.toDouble()
        _displayPercentages.value = if (totalWeight > 0) {
            _tasks.value.map { (it.weight / totalWeight) * usage }
        } else {
            List(_tasks.value.size) { 0.0 }
        }
    }

    fun getVisiblePercentage(visiblePosition: Int): Double =
        _displayPercentages.value.getOrNull(getRealIndex(visiblePosition)) ?: 0.0
}
