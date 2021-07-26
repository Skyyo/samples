package com.skyyo.composespacex.features.dog

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun DogFeedScreen(viewModel: DogFeedViewModel = hiltViewModel()) {
    val dogStatus = viewModel.dogStatus.observeAsState()
    Column {
        Button(onClick = { viewModel.goDogAdopt("2211") }) {
            Text(text = "Dog Feed: go adopt!")
        }
        Text(text = "dog status: ${dogStatus.value}")
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