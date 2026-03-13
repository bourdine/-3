package com.lottttto.miner.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TermsScreen(onAgreed: () -> Unit) {
    var agreed by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Lottttto",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "User Agreement",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Text(
                text = getTermsText(),
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = agreed,
                onCheckedChange = { agreed = it }
            )
            Text(
                text = "I have read and agree to the Terms of Use and Privacy Policy",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Button(
            onClick = onAgreed,
            enabled = agreed,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }
    }
}

private fun getTermsText(): String = """
    LOTTTTTO MINER USER AGREEMENT
    
    PLEASE READ THIS USER AGREEMENT CAREFULLY BEFORE USING THE APPLICATION.
    
    1. ACCEPTANCE OF TERMS
    By downloading, installing, or using the Lottttto Miner application ("App"), you agree to be bound by this User Agreement ("Agreement"). If you do not agree, do not use the App.
    
    2. DESCRIPTION OF SERVICE
    The App allows users to participate in cryptocurrency mining using their device's computing power. The App may include features such as wallet management, mining pool connections, and premium subscriptions.
    
    3. USER RESPONSIBILITIES
    You are solely responsible for:
    - The security of your wallet addresses and private keys.
    - Compliance with applicable laws regarding cryptocurrency mining.
    - Any consequences of mining, including device wear and electricity costs.
    
    4. FEES AND SUBSCRIPTIONS
    The App may offer premium subscriptions that reduce fees. All payments are processed through third-party payment providers.
    
    5. DISCLAIMER OF WARRANTIES
    THE APP IS PROVIDED "AS IS" WITHOUT WARRANTIES OF ANY KIND. WE DO NOT GUARANTEE THAT MINING WILL RESULT IN PROFITS OR THAT THE APP WILL BE UNINTERRUPTED.
    
    6. LIMITATION OF LIABILITY
    TO THE MAXIMUM EXTENT PERMITTED BY LAW, WE SHALL NOT BE LIABLE FOR ANY INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING FROM YOUR USE OF THE APP.
    
    7. CHANGES TO AGREEMENT
    We may update this Agreement from time to time. Continued use of the App constitutes acceptance of the new terms.
    
    8. GOVERNING LAW
    This Agreement shall be governed by the laws of [Your Country].
    
    Last updated: April 2025
""".trimIndent()
