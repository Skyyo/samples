package com.skyyo.samples.features.navigateWithResult.simple.dogFeed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun DogFeedScreen(viewModel: DogFeedViewModel = hiltViewModel()) {
    val dog = viewModel.dogStatus.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        Button(viewModel::goDogDetails, Modifier.statusBarsPadding()) {
            Text(text = "Dog Feed: go adopt!")
        }
        Text(text = "dog status: ${dog.value}")
    }
}
