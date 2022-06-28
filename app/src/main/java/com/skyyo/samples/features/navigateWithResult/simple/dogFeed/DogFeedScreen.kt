package com.skyyo.samples.features.navigateWithResult.simple.dogFeed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun DogFeedScreen(viewModel: DogFeedViewModel = hiltViewModel()) {
    val dog = viewModel.dogStatus.collectAsState()

    Column(Modifier.fillMaxSize()) {
        Button(viewModel::goDogDetails, Modifier.statusBarsPadding()) {
            Text(text = "Dog Feed: go adopt!")
        }
        Text(text = "dog status: ${dog.value}")
    }
}
