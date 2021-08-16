package com.skyyo.igdbbrowser.features.games

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyyo.igdbbrowser.features.launches.LaunchesListViewModel
import com.skyyo.igdbbrowser.features.launches.PastLaunchItem

@Composable
fun GamesList(viewModel: LaunchesListViewModel = hiltViewModel()) {
    val games = viewModel.games.collectAsState()
    LazyColumn {
//        latestLaunch.value?.let { launch -> item { LatestLaunchItem(launch) } }
//        itemsIndexed(upcomingLaunches.value){
//
//        }
//        latestLaunch.value?.let { item { LatestLaunchItem(it) } }
        itemsIndexed(games.value) { index, launch ->
//            PastLaunchItem(launch)
        }
    }
}