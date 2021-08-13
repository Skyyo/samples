package com.skyyo.igdbbrowser.features.profile

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun Profile(
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    Text(text = "Profile")
    Button(onClick = viewModel::onBtnClick) {

    }
}