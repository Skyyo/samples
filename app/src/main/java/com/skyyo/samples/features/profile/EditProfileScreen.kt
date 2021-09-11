package com.skyyo.samples.features.profile

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EditProfileScreen(
    profileSharedViewModel: ProfileSharedViewModel = hiltViewModel()
) {
    profileSharedViewModel.toString()
    Text(text = "edit Profile")
    Button(onClick = { profileSharedViewModel.goProfileConfirmation() }) {

    }
}