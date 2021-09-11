package com.skyyo.samples.features.pagination.paging

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.skyyo.samples.application.models.remote.Game
import com.skyyo.samples.common.composables.CircularProgressIndicatorRow
import com.skyyo.samples.extensions.toast
import com.skyyo.samples.features.pagination.common.CustomCard
import com.skyyo.samples.features.pagination.common.FadingFab
import com.skyyo.samples.features.pagination.common.GamesScreenEvent
import com.skyyo.samples.theme.DarkGray
import com.skyyo.samples.theme.White
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GamesPagingScreen(viewModel: GamesPagingViewModel = hiltViewModel()) {
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

    val games: LazyPagingItems<Game> = viewModel.games.collectAsLazyPagingItems()
    val isRefreshing by remember { derivedStateOf { games.loadState.refresh is LoadState.Loading } }
    val isErrorOnFirstPage by remember { derivedStateOf { games.loadState.refresh is LoadState.Error } }
    val isError by remember { derivedStateOf { games.loadState.append is LoadState.Error } }

    SideEffect {
        if (isErrorOnFirstPage) {
            val errorState = games.loadState.refresh as LoadState.Error
            viewModel.onGamesLoadingError(errorState.error.message!!.toInt())
            return@SideEffect // Just to prevent 2x toasts
        }
        if (isError) {
            val errorState = games.loadState.append as LoadState.Error
            viewModel.onGamesLoadingError(errorState.error.message!!.toInt())
        }
    }

    LaunchedEffect(Unit) {
        launch {
            events.collect { event ->
                when (event) {
                    is GamesScreenEvent.ShowToast -> context.toast(event.messageId)
                    is GamesScreenEvent.ScrollToTop -> coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                    GamesScreenEvent.RefreshList -> games.refresh()
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
            GamesColumn(listState = listState, games = games)
            FadingFab(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                isListScrolled = isListScrolled,
                onclick = viewModel::onScrollToTopClick
            )
        }
    }

}

@Composable
fun GamesColumn(
    listState: LazyListState,
    games: LazyPagingItems<Game>
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
        // we know that we're refreshing first page
        if (games.loadState.refresh is LoadState.Loading) {
            item {
                Text(
                    text = "refreshing on page 0",
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }

        // invoked when we error on initial load
        if (games.loadState.refresh is LoadState.Error) {
            val errorState = games.loadState.refresh as LoadState.Error
            item {
                Text(
                    text = stringResource(errorState.error.message!!.toInt()),
                    modifier = Modifier.clickable(onClick = games::retry)
                )
                Text(text = "retry refresh!")
            }
        }

        items(games, Game::id) { game -> if (game != null) CustomCard(gameName = game.name) }

        // we know that we're refreshing X page
        if (games.loadState.append is LoadState.Loading) {
            item { CircularProgressIndicatorRow() }
        }

        // invoked when we have no error on X page
        if (games.loadState.append is LoadState.Error) {
            val errorState = games.loadState.append as LoadState.Error
            item {
                Text(
                    text = stringResource(errorState.error.message!!.toInt()),
                    modifier = Modifier.clickable(onClick = games::retry)
                )
                Text(text = "retry append!")
            }
        }

    }
}