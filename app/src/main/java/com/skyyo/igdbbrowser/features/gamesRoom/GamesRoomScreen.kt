package com.skyyo.igdbbrowser.features.gamesRoom

import android.graphics.drawable.shapes.Shape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.skyyo.igdbbrowser.application.models.remote.Game
import com.skyyo.igdbbrowser.extensions.toast
import com.skyyo.igdbbrowser.features.games.GamesEvent
import com.skyyo.igdbbrowser.theme.DarkGray
import com.skyyo.igdbbrowser.theme.Purple500
import com.skyyo.igdbbrowser.theme.Shapes
import com.skyyo.igdbbrowser.theme.White
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GamesRoomScreen(viewModel: GamesRoomViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val insets = LocalWindowInsets.current
    val density = LocalDensity.current

    val insetTop: Dp = remember {
        with(density) { insets.statusBars.top.toDp() + 8.dp }
    }
    val listState = rememberLazyListState()
    val isListScrolled by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
    val coroutineScope = rememberCoroutineScope()
    val events = remember(viewModel.events, lifecycleOwner) {
        viewModel.events.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
    }

    val games by viewModel.games.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(Unit) {
        launch {
            events.collect { event ->
                when (event) {
                    is GamesEvent.NetworkError -> context.toast("network error")
                    is GamesEvent.ScrollToTop -> coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
            }
        }

    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = viewModel::onSwipeToRefresh,
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshingOffset = insetTop,
                refreshTriggerDistance = trigger,
                scale = true,
                backgroundColor = DarkGray,
                contentColor = White
            )
        }
    ) {
        Box(Modifier.fillMaxSize()) {
            GamesColumn(
                listState = listState,
                games = games,
                isLastPageReached = viewModel.isLastPageReached,
                onLastItemVisible = viewModel::getGames
            )
            AnimatedVisibility(
                enter = fadeIn(),
                exit = fadeOut(),
                visible = isListScrolled,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                FloatingActionButton(
                    viewModel::onScrollToTopClick,
                    modifier = Modifier.size(48.dp),
                    backgroundColor = DarkGray
                ) {
                    Icon(Icons.Filled.ArrowUpward, contentDescription = null, tint = White)
                }
            }
        }
    }

}

@Composable
fun GamesColumn(
    listState: LazyListState,
    games: List<Game>,
    isLastPageReached: Boolean,
    onLastItemVisible: () -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = true,
            applyBottom = false,
            additionalStart = 16.dp,
            additionalEnd = 16.dp,
            additionalBottom = 8.dp
        )
    ) {
        itemsIndexed(games) { index, game ->
            Card(
                backgroundColor = Purple500,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Shapes.medium)
                    .padding(vertical = 8.dp),
                shape = Shapes.large
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 56.dp)
                ) {
                    Image(painter = rememberImagePainter(
                        data = "https://placekitten.com/g/200/300",
                        builder = {
                            transformations(CircleCropTransformation())
                        }
                    ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(56.dp)
                            .padding(horizontal = 8.dp))
                    Text(game.name, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }
            if (!isLastPageReached && index == games.lastIndex) {
                onLastItemVisible()
//                SideEffect { onLastItemVisible() }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    content = { CircularProgressIndicator(color = Purple500) }
                )
            }
        }
    }
}