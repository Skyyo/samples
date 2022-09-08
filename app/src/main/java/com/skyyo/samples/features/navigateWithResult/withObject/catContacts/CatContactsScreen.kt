package com.skyyo.samples.features.navigateWithResult.withObject.catContacts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CatContactsScreen(viewModel: CatContactsViewModel = hiltViewModel()) {
    Box(Modifier.fillMaxSize()) {
        Button(viewModel::popToCatFeed, Modifier.statusBarsPadding()) {
            Text(text = "Cat contacts: ${viewModel.cat.name} go back")
        }
    }
}
