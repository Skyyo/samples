package com.skyyo.igdbbrowser.features.samples.navigateWithResult

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.statusBarsPadding
import com.skyyo.igdbbrowser.application.models.local.Dog


@Composable
fun DogFeedScreen(viewModel: DogFeedViewModel = hiltViewModel()) {
    val dog = viewModel.dog.observeAsState()
    Column {
        Button(
            onClick = {
//            viewModel.goDogAdopt("2211")
            viewModel.goDogAdoptWithObject(Dog(99, "Huskar"))
        },modifier = Modifier.statusBarsPadding()) {
            Text(text = "Dog Feed: go adopt!")
        }
        Text(text = "dog status: ${dog.value?.name}")
    }
}

@Composable
fun DogDetailsScreen(viewModel: DogDetailsViewModel = hiltViewModel()) {
    //TODO check if remember actually is usefull for non-changing VM stored fields
    val dog = remember { viewModel.dog }
    Button(onClick =
    {
//        viewModel.goContacts()
        viewModel.goContactsWithObject()
    }
    ) {
        Text("Dog Details: ${dog.id}  go contacts!")
    }
}

@Composable
fun DogContactsScreen(viewModel: DogContactsViewModel = hiltViewModel()) {
    val dog = remember { viewModel.dog }
    Button(onClick = viewModel::popToDogFeed) {
        Text(text = "Dog contacts: ${dog.name} go back")
    }
}