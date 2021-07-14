package com.skyyo.composespacex.features.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable


@Composable
fun AuthScreen(openDogFeedScreen: () -> Unit) {

    Column {
        Text(text = "Auth Screen")
        Button(onClick = { openDogFeedScreen() }) {
            Text(text = "press to imitate sign in")
        }
    }

}