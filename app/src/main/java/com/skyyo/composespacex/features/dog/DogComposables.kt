package com.skyyo.composespacex.features.dog

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable


@Composable
fun DogFeedScreen(openAdoptionScreen: (dogId: String) -> Unit) {
    Button(onClick = { openAdoptionScreen("2211") }) {
        Text(text = "Dog Feed: go adopt!")
    }
}

@Composable
fun DogDetailsScreen(dogId: String, openContactsScreen: () -> Unit) {
    Button(onClick = openContactsScreen) {
        Text("Dog Details: $dogId  go contacts!")
    }
}

@Composable
fun DogContactsScreen(dogId: String, navigateUp: () -> Unit) {
    Button(onClick = navigateUp) {
        Text(text = "Dog contacts: $dogId go back")
    }
}