package com.lottttto.miner.models

data class Pool(
    val coin: CoinType,
    val name: String,
    val url: String,
    val description: String
)
