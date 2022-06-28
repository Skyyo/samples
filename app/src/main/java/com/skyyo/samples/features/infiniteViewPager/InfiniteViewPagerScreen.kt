package com.skyyo.samples.features.infiniteViewPager

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.*
import com.skyyo.samples.extensions.log
import com.skyyo.samples.features.viewPager.PagerCard
import com.skyyo.samples.utils.floorMod
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

private const val INFINITE_PAGER_MAX_PAGE_COUNT = 100000
const val INFINITE_PAGER_INITIAL_PAGE = INFINITE_PAGER_MAX_PAGE_COUNT / 2

@ExperimentalPagerApi
@Composable
fun InfiniteViewPagerScreen() {
    val scope = rememberCoroutineScope()
    val pages = remember { listOf(1, 2, 3, 4, 5, 6) }
    val pagerState = rememberPagerState(
        pageCount = INFINITE_PAGER_MAX_PAGE_COUNT,
        initialPage = INFINITE_PAGER_INITIAL_PAGE,
        initialOffscreenLimit = 3 // to preload more items
    )
    val backgroundColor by animateColorAsState(
        targetValue = lerp(
            start = MaterialTheme.colors.primary,
            stop = MaterialTheme.colors.secondary,
            fraction = with(pagerState) { (currentPageOffset + currentPage) / pageCount }
        ),
        animationSpec = tween(durationMillis = 250, easing = LinearEasing)
    )

    LaunchedEffect(pagerState) {
        snapshotFlow {
            pagerState.currentPage
        }.collect { page ->
            val currentPage = (page - INFINITE_PAGER_INITIAL_PAGE).floorMod(pages.size)
            log("current page $currentPage")
        }
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) { page ->
            key(page) {
                val currentPage = (page - INFINITE_PAGER_INITIAL_PAGE).floorMod(pages.size)
                PagerCard(
                    pageOffset = calculateCurrentOffsetForPage(page).absoluteValue,
                    page = currentPage,
                    offset = 0.dp.value,
                    backgroundColor = backgroundColor,
                    onClick = { scope.launch { pagerState.animateScrollToPage(INFINITE_PAGER_INITIAL_PAGE) } }
                )
            }
        }
        HorizontalInfinitePagerIndicator(
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp),
            pagerState = pagerState,
            actualPageCount = pages.size
        )
    }
}
