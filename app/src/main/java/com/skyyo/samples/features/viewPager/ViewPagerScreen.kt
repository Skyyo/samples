package com.skyyo.samples.features.viewPager

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CarRepair
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.*
import com.skyyo.samples.extensions.log
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@ExperimentalPagerApi
@Composable
fun ViewPagerScreen() {
    val pages = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val pagerState = rememberPagerState(pageCount = pages.size)
    val scope = rememberCoroutineScope()
//    val pagerState = rememberPagerState(
//        pageCount = 10, // all others will be killed & recreated
//        initialOffscreenLimit = 2, // to preload more items
//    )
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            log("current page $page")
        }
    }
    val backgroundColor by animateColorAsState(
        targetValue = androidx.compose.ui.graphics.lerp(
            start = MaterialTheme.colors.primary,
            stop = MaterialTheme.colors.secondary,
            fraction = with(pagerState) { (currentPageOffset + currentPage) / pageCount }
        ),
        animationSpec = tween(durationMillis = 250, easing = LinearEasing)
    )
    Column(modifier = Modifier.statusBarsPadding()) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
//                    color = Color.White,
                    modifier = Modifier
                        .pagerTabIndicatorOffset(pagerState, tabPositions)
                        .clip(RoundedCornerShape(50)),
                    height = 2.dp
                )
            },
            backgroundColor = Color.Unspecified
        ) {
            // Add tabs for all of our pages
            pages.forEachIndexed { index, title ->
                val cornerSizeInPercents by animateIntAsState(if (index == pagerState.currentPage) 50 else 25)
                Tab(
                    modifier = Modifier.background(
                        backgroundColor,
                        RoundedCornerShape(cornerSizeInPercents)
                    ),
                    text = { Text("$title") },
//                    selectedContentColor = Color.Yellow,
//                    unselectedContentColor = Color.Green,
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
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
                            .background(backgroundColor)
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
        CustomHorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            indicatorWidth = 16.dp,
            activeIndicator = {
                Image(
                    modifier = Modifier.matchParentSize(),
                    imageVector = Icons.Rounded.CarRepair,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White)
                )
            },
            inactiveIndicator = {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(2.dp))
                        .background(backgroundColor)
                )
            }
        )
    }
}
