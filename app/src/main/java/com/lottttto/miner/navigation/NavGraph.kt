package com.lottttto.miner.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lottttto.miner.models.CoinType
import com.lottttto.miner.screens.auth.*
import com.lottttto.miner.screens.main.MainScreen
import com.lottttto.miner.screens.mining.MiningScreen
import com.lottttto.miner.screens.settings.AboutScreen
import com.lottttto.miner.screens.settings.MiningPoolsScreen
import com.lottttto.miner.screens.settings.MoneroMiningSettingsScreen
import com.lottttto.miner.screens.wallet.WalletScreen
import com.lottttto.miner.viewmodels.PinViewModel

@Composable
fun OnboardingNavGraph(onOnboardingComplete: () -> Unit) {
    val navController = rememberNavController()
    val viewModel: PinViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val startDestination = when {
        uiState.isFirstLaunch -> "terms"
        !uiState.isPinSet -> "set_pin"
        else -> "enter_pin"
    }

    NavHost(navController, startDestination) {
        composable("terms") {
            TermsScreen(
                onAgreed = {
                    navController.navigate("set_pin") {
                        popUpTo("terms") { inclusive = true }
                    }
                }
            )
        }
        composable("set_pin") {
            SetPinScreen(
                onPinSet = {
                    viewModel.markPinValidated()
                    onOnboardingComplete()
                },
                viewModel = viewModel
            )
        }
        composable("enter_pin") {
            EnterPinScreen(
                onPinSuccess = {
                    viewModel.markPinValidated()
                    onOnboardingComplete()
                },
                onForgotPin = {
                    viewModel.logout()
                    navController.popBackStack("terms", inclusive = false)
                },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun AppNavGraph(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController, "main") {
        composable("main") {
            MainScreen(
                onNavigateToWallet = { navController.navigate("wallet") },
                onNavigateToMiningSettings = { navController.navigate("monero_mining_settings") },
                onNavigateToMining = { navController.navigate("mining") },
                onNavigateToAbout = { navController.navigate("about") },
                onLogout = onLogout
            )
        }
        composable("wallet") { WalletScreen(onBack = { navController.popBackStack() }) }
        composable("monero_mining_settings") {
            MoneroMiningSettingsScreen(onBack = { navController.popBackStack() })
        }
        composable("mining_pools") {
            MiningPoolsScreen(onBack = { navController.popBackStack() })
        }
        composable("mining") {
            MiningScreen(onBack = { navController.popBackStack() })
        }
        composable("about") {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}
