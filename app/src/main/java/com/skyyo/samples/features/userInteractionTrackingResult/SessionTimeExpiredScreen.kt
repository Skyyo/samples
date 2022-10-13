package com.skyyo.samples.features.userInteractionTrackingResult

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

// TODO this solution is not tracking continuous scroll without removing the finger event. Enhance
@Composable
fun SessionTimeExpiredScreen(viewModel: SessionTimeExpiredViewModel = hiltViewModel()) {
    BackHandler(true) {}
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "You have been idle for $SESSION_TIME_IN_SECONDS seconds.",
            fontSize = 20.sp
        )
        Button(
            modifier = Modifier.padding(top = 20.dp),
            onClick = viewModel::onContinueButtonClick
        ) {
            Text(text = "Continue using the app")
        }
        Button(modifier = Modifier.padding(top = 8.dp), onClick = viewModel::onQuitButtonClick) {
            Text(text = "Close screen without restarting session")
        }
    }
}
