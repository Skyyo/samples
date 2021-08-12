package com.skyyo.composespacex.features.samples

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SamplesScreenList(viewModel: SamplesViewModel = hiltViewModel()) {
    Column {
        Button(onClick = {}) {
            Text("Navigation Drawer")
        }
    }

}
