package com.skyyo.samples.features.sharedViewModel.confirmProfile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.statusBarsPadding
import com.skyyo.samples.features.sharedViewModel.ProfileSharedViewModel

@Composable
fun EditProfileConfirmationScreen(profileSharedViewModel: ProfileSharedViewModel) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Text(text = "EditProfileConfirmation")
        Button(onClick = profileSharedViewModel::popToStart) {
            Text(text = "pop to start")
        }
    }
}