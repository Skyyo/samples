package com.skyyo.igdbbrowser.features.samples.navigateWithResult

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.statusBarsPadding
import com.skyyo.igdbbrowser.application.models.local.Dog


@Composable
fun DogFeedScreen(viewModel: DogFeedViewModel = hiltViewModel()) {
    val dog = viewModel.dog.observeAsState()
    Column(Modifier.fillMaxSize()) {
        Button(
            onClick = {
//            viewModel.goDogAdopt("2211")
                viewModel.goDogAdoptWithObject(Dog(99, "Huskar"))
            }, modifier = Modifier.statusBarsPadding()
        ) {
            Text(text = "Dog Feed: go adopt!")
        }
        Text(text = "dog status: ${dog.value?.name}")
    }
}



