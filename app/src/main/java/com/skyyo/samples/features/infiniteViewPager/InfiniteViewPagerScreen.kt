package com.skyyo.samples.features.infiniteViewPager

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.skyyo.samples.extensions.log
import com.skyyo.samples.features.transparentBlur.background
import com.skyyo.samples.features.viewPager.PagerCard
import com.skyyo.samples.utils.floorMod
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.system.measureNanoTime

private const val INFINITE_PAGER_MAX_PAGE_COUNT = 100000
const val INFINITE_PAGER_INITIAL_PAGE = INFINITE_PAGER_MAX_PAGE_COUNT / 2
private const val TAG = "InfiniteViewPagerScreen"
private const val COUNT_COMPARISON_FOR_AND_FAST_FOR = 10

@ExperimentalPagerApi
@Composable
fun InfiniteViewPagerScreen() {
    val scope = rememberCoroutineScope()
    val pages = remember { listOf(1, 2, 3, 4, 5) }
    val infinitePages = CircularList(listOf(1, 2/*, 3, 4, 5*/))
    val longCircularList = CircularList(List(1000) { it })
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

    LaunchedEffect(Unit) {
        repeat(COUNT_COMPARISON_FOR_AND_FAST_FOR) {
            var sum = 0
            val forTime = measureNanoTime { longCircularList.forEach { sum += it } }
            sum = 0
            val fastForTime = measureNanoTime { longCircularList.fastForEach { sum += it } }
            Log.d(TAG, "forEach: $forTime, fastForEach: $fastForTime")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
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
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            pagerState = pagerState,
            actualPageCount = pages.size
        )

        InfiniteViewPager(
            modifier = Modifier.padding(top = 10.dp),
            state = remember { InfiniteViewPagerState(infinitePages) }
        ) { item ->
            val color = remember(item) {
                when (item) {
                    1 -> Color.Red
                    2 -> Color.Blue
                    3 -> Color.Green
                    4 -> Color.Yellow
                    else -> Color.DarkGray
                }
            }
            Text(
                text = "text$item",
                style = TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.W400),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .background(color)
            )
        }
    }
}
