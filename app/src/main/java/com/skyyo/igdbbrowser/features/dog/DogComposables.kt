package com.skyyo.igdbbrowser.features.dog

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
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
fun DogDetailsScreen(viewModel: DogDetailsViewModel = hiltViewModel()) {
    //TODO check if remember actually is usefull for non-changing VM stored fields
    val dogId = remember { viewModel.dogId }
    Button(onClick = viewModel::goContacts) {
        Text("Dog Details: $dogId  go contacts!")
    }
}

@Composable
fun DogContactsScreen(viewModel: DogContactsViewModel = hiltViewModel()) {
    val dogId = remember { viewModel.dogId }
    Button(onClick = viewModel::popToDogFeed) {
        Text(text = "Dog contacts: $dogId go back")
    }
}