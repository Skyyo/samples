package com.skyyo.samples.features.sharedViewModel.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.statusBarsPadding
import com.skyyo.samples.features.sharedViewModel.ProfileSharedViewModel

@Composable
fun ProfileScreen(profileSharedViewModel: ProfileSharedViewModel = hiltViewModel()) {
    Column(Modifier.fillMaxSize()) {
        Text(text = "Profile Screen", modifier = Modifier.statusBarsPadding())
        Button(onClick = profileSharedViewModel::goEditProfile) {
            Text("go edit profile")
        }
    }
}