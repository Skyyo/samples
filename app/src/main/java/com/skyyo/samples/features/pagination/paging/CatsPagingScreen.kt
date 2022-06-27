package com.skyyo.samples.features.pagination.paging

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.skyyo.samples.application.models.Cat
import com.skyyo.samples.common.composables.CircularProgressIndicatorRow
import com.skyyo.samples.extensions.collect
import com.skyyo.samples.extensions.toast
import com.skyyo.samples.features.pagination.common.CatsScreenEvent
import com.skyyo.samples.features.pagination.common.CustomCard
import com.skyyo.samples.features.pagination.common.FadingFab
import com.skyyo.samples.theme.DarkGray
import com.skyyo.samples.theme.White
import com.skyyo.samples.utils.OnClick

@Composable
fun CatsPagingScreen(viewModel: CatsPagingViewModel = hiltViewModel()) {
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

    val cats: LazyPagingItems<Cat> = viewModel.cats.collect()
    val isRefreshInProgress by remember { viewModel.isRefreshInProgress }
    val firstPageError by remember { viewModel.firstPageError }
    val currentPageError by remember { viewModel.currentPageError }
    val newPageLoading by remember { viewModel.newPageLoading }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is CatsScreenEvent.ShowToast -> context.toast(event.messageId)
                is CatsScreenEvent.ScrollToTop -> listState.animateScrollToItem(0)
            }
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshInProgress),
        onRefresh = viewModel::refresh,
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
                isRefreshInProgress = isRefreshInProgress,
                firstPageError = firstPageError,
                currentPageError = currentPageError,
                newPageLoading = newPageLoading,
                cats = cats,
                onRetryClick = viewModel::onRetryClick
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
    isRefreshInProgress: Boolean,
    firstPageError: Int?,
    currentPageError: Int?,
    newPageLoading: Boolean,
    cats: LazyPagingItems<Cat>,
    onRetryClick: OnClick
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = true,
            applyBottom = true,
            additionalStart = 16.dp,
            additionalEnd = 16.dp,
            additionalBottom = 8.dp
        )
    ) {
        //refreshing on page 0
        if (isRefreshInProgress) {
            item {
                Text(
                    text = "refreshing on page 0",
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }

        if (firstPageError != null) {
            item {
                Text(
                    text = "First: " + stringResource(id = firstPageError),
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .clickable(onClick = onRetryClick)
                )
            }
        }

        items(cats, Cat::id) { cat -> if (cat != null) CustomCard(catId = cat.id) }

        // we know that we're refreshing X page
        if (newPageLoading) {
            item { CircularProgressIndicatorRow() }
        }

        if (currentPageError != null) {
            item {
                Text(
                    text = stringResource(id = currentPageError),
                    modifier = Modifier.padding(top = 10.dp).clickable(onClick = onRetryClick)
                )
            }
        }
    }
}