package com.skyyo.composespacex.features.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun Profile(viewModel: ProfileViewModel = hiltViewModel()) {
    Column {
        Text(text = "Profile")
        Text(text = "Id ${viewModel.profileId}")
    }
}