package com.skyyo.samples.features.profile

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun EditProfileConfirmationScreen(profileSharedViewModel: ProfileSharedViewModel) {
    profileSharedViewModel.toString()
    Text(text = "EditProfileConfirmation")
    Button(onClick = profileSharedViewModel::goProfileConfirmation2) {

    }
}