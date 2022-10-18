package com.skyyo.samples.features.navigateWithResult.simple.dogFeed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DogFeedScreen(viewModel: DogFeedViewModel = hiltViewModel()) {
    val dog = viewModel.dogStatus.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { viewModel.onEndAnimationRectChanged(it.boundsInRoot()) }
    ) {
        Button(
            onClick = viewModel::goDogDetails,
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 40.dp, top = 100.dp)
                .onGloballyPositioned {
                    viewModel.onStartAnimationRectChanged(it.boundsInRoot())
                }
        ) {
            Text(text = "Dog Feed: go adopt!")
        }
        Text(text = "dog status: ${dog.value}")
    }
}
