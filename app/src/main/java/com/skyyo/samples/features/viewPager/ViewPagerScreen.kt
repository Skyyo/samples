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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.*
import com.skyyo.samples.extensions.log
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.max

@Suppress("LongMethod")
@ExperimentalPagerApi
@Composable
fun ViewPagerScreen() {
    val scope = rememberCoroutineScope()
    val pages = remember { listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) }
    val pagerState = rememberPagerState(
        pageCount = pages.size,
        initialPageOffset = 1f,
        initialOffscreenLimit = 3, // to preload more items
    )
    val backgroundColor by animateColorAsState(
        targetValue = lerp(
            start = MaterialTheme.colors.primary,
            stop = MaterialTheme.colors.secondary,
            fraction = with(pagerState) { (currentPageOffset + currentPage) / pageCount }
        ),
        animationSpec = tween(durationMillis = 250, easing = LinearEasing)
    )
    val indicatorShape = remember {
        RoundedCornerShape(percent = 50)
    }

    LaunchedEffect(pagerState) {
        snapshotFlow {
            pagerState.currentPage
        }.collect { page ->
            log("current page $page")
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Color.Unspecified,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier
                        .scrollableTabIndicatorOffset(pagerState, tabPositions)
                        .clip(indicatorShape),
                    height = 2.dp
                )
            },
            tabs = {
                pages.forEachIndexed { index, title ->
                    val cornerSizeInPercents by animateIntAsState(
                        if (index == pagerState.currentPage) 100 else 25,
                        animationSpec = tween(durationMillis = 1000)
                    )
                    key(index) {
                        Tab(
                            modifier = Modifier
                                .padding(16.dp)
                                .background(
                                    backgroundColor,
                                    RoundedCornerShape(cornerSizeInPercents)
                                ),
                            text = { Text("$title") },
                            selectedContentColor = Color.White,
                            unselectedContentColor = Color.Black,
                            selected = pagerState.currentPage == index,
                            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        )
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
            key(page) {
                PagerCard(
                    pageOffset = this@HorizontalPager.calculateCurrentOffsetForPage(page).absoluteValue,
                    page = page,
                    offset = 0.dp.value,
                    backgroundColor = backgroundColor,
                    onClick = { scope.launch { pagerState.animateScrollToPage(0) } }
                )
//                PagerCard(
//                    pageOffset = this@HorizontalPager.calculateCurrentOffsetForPage(page).absoluteValue,
//                    page = page,
//                    offset = 120.dp.value,
//                    backgroundColor = backgroundColor,
//                    onClick = { scope.launch { pagerState.animateScrollToPage(0) } })
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

@ExperimentalPagerApi
fun Modifier.scrollableTabIndicatorOffset(pagerState: PagerState, tabPositions: List<TabPosition>): Modifier = composed {
    // If there are no pages, nothing to show
    if (pagerState.pageCount == 0) return@composed this

    val targetIndicatorOffset: Dp
    val indicatorWidth: Dp

    val currentPage = pagerState.currentPage
    val targetPage = pagerState.targetPage

    val currentTab = tabPositions[currentPage]
    val targetTab = tabPositions.getOrNull(targetPage)
    val scrollingToLeft = targetPage == currentPage

    if (targetTab != null) {
        // The distance between the target and current page. If the pager is animating over many
        // items this could be > 1
        val targetDistance = (targetPage - currentPage).absoluteValue
        // Our normalized fraction over the target distance
        val fraction = (pagerState.currentPageOffset / max(targetDistance, 1)).absoluteValue

        if (!pagerState.isScrollInProgress || !scrollingToLeft) {
            targetIndicatorOffset = lerp(
                tabPositions[currentPage].left,
                tabPositions[targetPage].left,
                fraction
            )
        } else {
            val endPage = fraction.toInt()
            targetIndicatorOffset = lerp(
                start = tabPositions[currentPage - endPage - if (currentPage - endPage > 0) 1 else 0].left,
                stop = tabPositions[currentPage - endPage].left,
                fraction = 1 - fraction + endPage
            )
        }

        indicatorWidth = lerp(
            tabPositions[currentPage].width,
            tabPositions[targetPage].width,
            fraction
        ).value.absoluteValue.dp
    } else {
        // Otherwise we just use the current tab/page
        targetIndicatorOffset = currentTab.left
        indicatorWidth = currentTab.width
    }

    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = targetIndicatorOffset)
        .width(indicatorWidth)
}
