package com.skyyo.samples.features.navigateWithResult.dogDetails

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun DogDetailsScreen(viewModel: DogDetailsViewModel = hiltViewModel()) {
    Button(viewModel::goContactsWithObject, Modifier.fillMaxSize().statusBarsPadding()) {
        Text("Dog Details: ${viewModel.dog.id}  go contacts!")
    }
}