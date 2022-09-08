package com.skyyo.samples.features.navigateWithResult.withObject.catDetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CatDetailsScreen(viewModel: CatDetailsViewModel = hiltViewModel()) {
    Box(Modifier.fillMaxSize()) {
        Button(viewModel::goCatContacts, Modifier.statusBarsPadding()) {
            Text("Cat Details: ${viewModel.cat.id}, go contacts!")
        }
    }
}
