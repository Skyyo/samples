package com.skyyo.igdbbrowser.features.launches

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyyo.igdbbrowser.application.models.remote.Launch


@Composable
fun LaunchesList(viewModel: LaunchesListViewModel = hiltViewModel()) {
//    val latestLaunch = viewModel.latestLaunch.collectAsState()
    val pastLaunches = viewModel.games.collectAsState()
    LazyColumn {
//        latestLaunch.value?.let { launch -> item { LatestLaunchItem(launch) } }
//        itemsIndexed(upcomingLaunches.value){
//
//        }
//        latestLaunch.value?.let { item { LatestLaunchItem(it) } }
        itemsIndexed(pastLaunches.value) { index, launch ->  }
    }
}

@Composable
fun LatestLaunchItem(launch: Launch) {
    Text(text = "latest launch: ${launch.name}")
}

@Composable
fun PastLaunchItem(launch: Launch) {
    Text(text = launch.name)
}

