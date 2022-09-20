package com.skyyo.samples.features.userInteractionTracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun UserInteractionTrackingScreen(viewModel: UserInteractionTrackingViewModel = hiltViewModel()) {
    val login by viewModel.login.collectAsState()
    val password by viewModel.password.collectAsState()
    val isAuthorized by viewModel.isAuthorized.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (isAuthorized) {
            null -> {
                TextField(value = login, onValueChange = viewModel::onLoginChange)
                TextField(value = password, onValueChange = viewModel::onPasswordChange)
                Button(onClick = viewModel::onLoginClick) {
                    Text(text = "Login")
                }
            }
            true -> {
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.h2,
                    textAlign = TextAlign.Center
                )
            }
            false -> {
                Text(
                    text = "Login or password is incorrect!",
                    style = MaterialTheme.typography.h2,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
