package com.skyyo.igdbbrowser.features.samples.viewPager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.google.accompanist.pager.*
import com.skyyo.igdbbrowser.extensions.log
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@ExperimentalPagerApi
@Composable
fun ViewPagerScreen() {
    val pagerState = rememberPagerState(pageCount = 10)
    val scope = rememberCoroutineScope()
    val pages = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
//    val pagerState = rememberPagerState(
//        pageCount = 10, // all others will be killed & recreated
//        initialOffscreenLimit = 2, // to preload more items
//    )
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            log("current page $page")
        }
    }

    Column {
        ScrollableTabRow(
            edgePadding = 0.dp,
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            // Add tabs for all of our pages
            pages.forEachIndexed { index, title ->
                Tab(
                    text = { Text("$title") },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(index) }
                    },
                )
            }
        }
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
            Card(Modifier.graphicsLayer {
                // Calculate the absolute offset for the current page from the
                // scroll position. We use the absolute value which allows us to mirror
                // any effects for both directions
                val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                // We animate the scaleX + scaleY, between 85% and 100%
                lerp(
                    start = 0.85f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                ).also { scale ->
                    scaleX = scale
                    scaleY = scale
                }

                // We animate the alpha, between 50% and 100%
                alpha = lerp(
                    start = 0.5f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                )
            }) {
                Column {
                    Button(onClick = {
                        scope.launch {
//                        pagerState.scrollToPage(0)
                            pagerState.animateScrollToPage(0)
                        }
                    }) {
                        Text("scroll to page 0 ")
                    }
                    Text(
                        text = "Page: $page",
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .fillMaxHeight(0.5f)
                            .background(Color.Cyan)
                    )
                }
            }
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
        )
    }
}
