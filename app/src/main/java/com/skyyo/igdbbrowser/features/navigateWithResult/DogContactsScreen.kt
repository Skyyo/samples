package com.skyyo.igdbbrowser.features.navigateWithResult

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun DogContactsScreen(viewModel: DogContactsViewModel = hiltViewModel()) {
    Button(viewModel::popToDogFeed, Modifier.statusBarsPadding()) {
        Text(text = "Dog contacts: ${viewModel.dog.name} go back")
    }
}