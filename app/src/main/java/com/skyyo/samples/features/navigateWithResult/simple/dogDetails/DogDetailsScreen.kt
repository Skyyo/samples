package com.skyyo.samples.features.navigateWithResult.simple.dogDetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun DogDetailsScreen(viewModel: DogDetailsViewModel = hiltViewModel()) {
    Box(Modifier.fillMaxSize()) {
        Button(viewModel::goContacts, Modifier.statusBarsPadding()) {
            Text("Dog Details: ${viewModel.dogId}  go contacts!")
        }
    }
}
