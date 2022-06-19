package com.skyyo.samples.features.infiniteViewPager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.skyyo.samples.utils.floorMod

@ExperimentalPagerApi
@Composable
fun HorizontalInfinitePagerIndicator(
    pagerState: PagerState,
    actualPageCount: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    inactiveColor: Color = activeColor.copy(ContentAlpha.disabled),
    indicatorWidth: Dp = 6.dp,
    indicatorHeight: Dp = 6.dp,
    spacing: Dp = indicatorWidth,
    indicatorShape: Shape = CircleShape,
) {
    val indicatorWidthPx = LocalDensity.current.run { indicatorWidth.roundToPx() }
    val spacingPx = LocalDensity.current.run { spacing.roundToPx() }
    val actualPage = (pagerState.currentPage - INFINITE_PAGER_INITIAL_PAGE).floorMod(actualPageCount)
    Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val indicatorModifier = Modifier
                .size(width = indicatorWidth, height = indicatorHeight)
                .background(color = inactiveColor, shape = indicatorShape)
            repeat(actualPageCount) {
                Box(indicatorModifier)
            }
        }
        Box(
            modifier = Modifier
                .offset {
                    val scrollPosition = (actualPage + pagerState.currentPageOffset).coerceIn(
                        minimumValue = 0f,
                        maximumValue = (actualPageCount - 1).coerceAtLeast(0).toFloat()
                    )
                    IntOffset(
                        x = ((spacingPx + indicatorWidthPx) * scrollPosition).toInt(),
                        y = 0
                    )
                }
                .size(width = indicatorWidth, height = indicatorHeight)
                .background(color = activeColor, shape = indicatorShape)
        )
    }
}
