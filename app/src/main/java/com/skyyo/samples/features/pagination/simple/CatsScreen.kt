package com.skyyo.samples.features.pagination.simple

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.skyyo.samples.application.models.Cat
import com.skyyo.samples.common.composables.CircularProgressIndicatorRow
import com.skyyo.samples.extensions.toast
import com.skyyo.samples.features.pagination.common.CatsScreenEvent
import com.skyyo.samples.features.pagination.common.CustomCard
import com.skyyo.samples.features.pagination.common.FadingFab
import com.skyyo.samples.theme.DarkGray
import com.skyyo.samples.theme.White

@Composable
fun CatsScreen(viewModel: CatsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val insets = LocalWindowInsets.current
    val density = LocalDensity.current

    val insetTop: Dp = remember {
        with(density) { insets.statusBars.top.toDp() + 8.dp }
    }
    val listState = rememberLazyListState()
    val isListScrolled by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
    val events = remember(viewModel.events, lifecycleOwner) {
        viewModel.events.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
    }

    val cats by viewModel.cats.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is CatsScreenEvent.ShowToast -> context.toast(event.messageId)
                is CatsScreenEvent.ScrollToTop -> listState.animateScrollToItem(0)
                else -> {}
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
            CatsColumn(
                listState = listState,
                cats = cats,
                isLastPageReached = viewModel.isLastPageReached,
                onLastItemVisible = viewModel::getCats
            )
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
fun CatsColumn(
    listState: LazyListState,
    cats: List<Cat>,
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
        itemsIndexed(cats, { _, cat -> cat.id }) { index, cat ->
            CustomCard(catId = cat.id)
            if (!isLastPageReached && index == cats.lastIndex) {
                SideEffect { onLastItemVisible() }
                CircularProgressIndicatorRow()
            }
        }
    }
}
