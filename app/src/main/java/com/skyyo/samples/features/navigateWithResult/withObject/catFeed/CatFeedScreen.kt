package com.skyyo.samples.features.navigateWithResult.withObject.catFeed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.statusBarsPadding
import com.skyyo.samples.features.navigateWithResult.withObject.catFeed.CatFeedViewModel as CatFeedViewModel1


@Composable
fun CatFeedScreen(viewModel: CatFeedViewModel1 = hiltViewModel()) {
    val cat = viewModel.cat.observeAsState()
    Column(Modifier.fillMaxSize()) {
        Button(viewModel::goCatDetails, Modifier.statusBarsPadding()) {
            Text(text = "Cat Feed: go details!")
        }
        Text(text = "cat name: ${cat.value?.name}")
    }
}



