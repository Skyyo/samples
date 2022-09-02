package com.skyyo.samples.features.userInteractionTrackingResult

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyyo.samples.application.activity.SESSION_EXTRA_TIME_SECONDS
import com.skyyo.samples.application.activity.SESSION_MAIN_TIME_SECONDS
import com.skyyo.samples.utils.scaleBetweenBounds

@Composable
fun UserInteractionTrackingResultScreen(
    viewModel: UserInteractionTrackingResultViewModel = hiltViewModel()
) {
    val areYouStillHereTime by viewModel.areYouStillHereTime.collectAsState()
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = areYouStillHereTime.scaleBetweenBounds(
                0f,
                SESSION_EXTRA_TIME_SECONDS.toFloat(),
                0f,
                1f
            )
        )
        Text(
            text = "You have been idle for $SESSION_MAIN_TIME_SECONDS seconds." +
                " Now you have $SESSION_EXTRA_TIME_SECONDS seconds to choose one of the following options",
            fontSize = 20.sp
        )
        Button(modifier = Modifier.padding(top = 20.dp), onClick = viewModel::navigateUp) {
            Text(text = "Go back")
        }
        Button(modifier = Modifier.padding(top = 8.dp), onClick = viewModel::goHome) {
            Text(text = "Go home")
        }
    }
}
