package com.skyyo.samples.features.navigateWithResult.withObject.catFeed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.skyyo.samples.features.navigateWithResult.withObject.catFeed.CatFeedViewModel as CatFeedViewModel1

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun CatFeedScreen(viewModel: CatFeedViewModel1 = hiltViewModel()) {
    val cat = viewModel.cat.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize()) {
        Button(viewModel::goCatDetails, Modifier.statusBarsPadding()) {
            Text(text = "Cat Feed: go details!")
        }
        Text(text = "cat name: ${cat.value?.name}")
    }
}
