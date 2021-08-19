package com.skyyo.igdbbrowser.features.samples.navigateWithResult

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DogDetailsScreen(viewModel: DogDetailsViewModel = hiltViewModel()) {
    Button(viewModel::goContactsWithObject) {
        Text("Dog Details: ${viewModel.dog.id}  go contacts!")
    }
}