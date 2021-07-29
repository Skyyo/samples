package com.skyyo.composespacex.features.profile

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EditProfile(
    sharedProfileViewModel: SharedProfileViewModel = hiltViewModel())
 {
    sharedProfileViewModel.toString()
    Text(text = "edit Profile")
    Button(onClick = { sharedProfileViewModel.goProfileConfirmation() }) {
        
    }
}