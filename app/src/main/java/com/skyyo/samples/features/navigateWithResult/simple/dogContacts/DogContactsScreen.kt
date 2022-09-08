package com.skyyo.samples.features.navigateWithResult.simple.dogContacts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DogContactsScreen(viewModel: DogContactsViewModel = hiltViewModel()) {
    Box(Modifier.fillMaxSize()) {
        Button(viewModel::popToDogFeed, Modifier.statusBarsPadding()) {
            Text(text = "Dog contacts: ${viewModel.dogId} go back")
        }
    }
}
