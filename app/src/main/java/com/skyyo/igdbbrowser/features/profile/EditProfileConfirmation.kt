package com.skyyo.igdbbrowser.features.profile

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun EditProfileConfirmation(sharedProfileViewModel: SharedProfileViewModel) {
    sharedProfileViewModel.toString()
    Text(text = "EditProfileConfirmation")
    Button(onClick = sharedProfileViewModel::goProfileConfirmation2) {

    }
}