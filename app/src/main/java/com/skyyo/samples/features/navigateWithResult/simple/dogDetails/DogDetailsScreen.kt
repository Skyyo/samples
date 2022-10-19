package com.skyyo.samples.features.navigateWithResult.simple.dogDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DogDetailsScreen(viewModel: DogDetailsViewModel = hiltViewModel()) {
    Box(Modifier.fillMaxSize().background(Color.Magenta)) {
        Button(viewModel::goContacts, Modifier.statusBarsPadding()) {
            Text("Dog Details: ${viewModel.dogId}  go contacts!")
        }
    }
}
