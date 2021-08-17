package com.skyyo.igdbbrowser.features.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.extensions.toast
import com.skyyo.igdbbrowser.theme.Shapes
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun GamesScreen(viewModel: GamesListViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val events = remember(viewModel.events, lifecycleOwner) {
        viewModel.events.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
    }
    val games = viewModel.games.collectAsState()

    LaunchedEffect(Unit) {
        launch {
            events.collect { event ->
                when (event) {
                    is GamesListEvent.NetworkError -> context.toast("network error")
                }
            }
        }

    }

    GamesColumn(
        games = games.value,
        isFetchingAllowed = viewModel.isFetchingAllowed,
        onLastItemVisible = viewModel::getGames
    )
}

@Composable
fun GamesColumn(
    games: List<Game>,
    isFetchingAllowed: Boolean,
    onLastItemVisible: () -> Unit
) {
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Text("Games list")
        }
        itemsIndexed(games) { index, game ->
            Card(
                backgroundColor = Color.Magenta,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(16.dp),
                shape = Shapes.large
            ) {
                Text(game.name)
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (isFetchingAllowed && index == games.lastIndex) {
                onLastItemVisible()
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    content = { CircularProgressIndicator() }
                )
            }
        }
    }
}
