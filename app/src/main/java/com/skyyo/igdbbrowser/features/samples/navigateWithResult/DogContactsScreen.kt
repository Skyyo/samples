package com.skyyo.igdbbrowser.features.samples.navigateWithResult

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DogContactsScreen(viewModel: DogContactsViewModel = hiltViewModel()) {
    Button(viewModel::popToDogFeed) {
        Text(text = "Dog contacts: ${viewModel.dog.name} go back")
    }
}