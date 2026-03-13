package com.lottttto.miner.models

data class MiningTask(
    val coin: CoinType,
    val mode: MiningMode,
    var weight: Int = 0
)

data class MiningStats(
    val coin: CoinType,
    val hashrate: Double,
    val acceptedShares: Long,
    val rejectedShares: Long,
    val estimatedEarnings: Double
)
