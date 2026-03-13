package com.lottttto.miner.models

data class Wallet(
    val id: Long = 0,
    val address: String,
    val coin: CoinType,
    val label: String? = null,
    val seedPhrase: String? = null
)
