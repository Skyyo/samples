package com.skyyo.composespacex.features.profile

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EditProfileConfirmation(sharedProfileViewModel: SharedProfileViewModel) {
    sharedProfileViewModel.toString()
    Text(text = "EditProfileConfirmation")
    Button(onClick = sharedProfileViewModel::goProfileConfirmation2) {

    }
}