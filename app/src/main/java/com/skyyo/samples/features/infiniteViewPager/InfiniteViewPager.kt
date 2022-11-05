package com.skyyo.samples.features.infiniteViewPager

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun <T> InfiniteViewPager(
    state: InfiniteViewPagerState<T>,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    itemSpacing: Dp = 0.dp,
    dragEnabled: Boolean = true,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    flingBehavior: FlingBehavior = rememberPagerFlingConfig(state),
    content: @Composable InfinitePagerScope<T>.(item: T) -> Unit,
) {
    // True if the scroll direction is RTL, false for LTR
    val reverseDirection = when {
        // If we're horizontal in RTL, flip reverseLayout
        LocalLayoutDirection.current == LayoutDirection.Rtl -> !reverseLayout
        // Else (horizontal in LTR), use reverseLayout as-is
        else -> reverseLayout
    }

    val coroutineScope = rememberCoroutineScope()
    val semanticsAxisRange = remember(state, reverseDirection) {
        ScrollAxisRange(
            value = { state.currentLayoutPageOffset },
            maxValue = { Float.POSITIVE_INFINITY },
        )
    }

    val semantics = Modifier.semantics {
        horizontalScrollAxisRange = semanticsAxisRange
        // Hook up scroll actions to our state
        scrollBy { x, _ ->
            coroutineScope.launch {
                state.scrollBy(if (reverseDirection) x else -x)
            }
            true
        }
        // Treat this as a selectable group
        selectableGroup()
    }

    val scrollable = Modifier.scrollable(
        orientation = Orientation.Horizontal,
        flingBehavior = flingBehavior,
        reverseDirection = reverseDirection,
        state = state,
        interactionSource = state.internalInteractionSource,
        enabled = dragEnabled,
    )

    Layout(
        modifier = modifier
            .then(semantics)
            .then(scrollable)
            // Add a NestedScrollConnection which consumes all post fling/scrolls
            .nestedScroll(connection = ConsumeFlingNestedScrollConnection),
        content = {

            // FYI: We need to filter out null/empty pages *outside* of the loop. Compose uses the
            // call stack as part of the key for state, so we need to ensure that the call stack
            // for page content is consistent as the user scrolls, otherwise content will
            // drop/recreate state.
            val pages = state.layoutPages
            pages.fastForEach { t ->
                key(t) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = PageData(t)
                    ) {
                        val scope = remember(this, state) {
                            InfinitePagerScopeImpl(this, state)
                        }
                        scope.content(t)
                    }
                }
            }
        },
    ) { measurables, constraints ->
        if (measurables.isEmpty()) {
            // If we have no measurables, no-op and return
            return@Layout layout(constraints.minWidth, constraints.minHeight) {}
        }
        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { it.measure(childConstraints) }
        // Our pager width/height is the maximum pager content width/height, and coerce
        // each by our minimum constraints
        val pagerWidth = placeables.maxOf { it.width }.coerceAtLeast(constraints.minWidth)
        val pagerHeight = placeables.maxOf { it.height }.coerceAtLeast(constraints.minHeight)

        state.layoutSize = pagerWidth
        val offset = state.currentLayoutPageOffset

        layout(width = pagerWidth, height = pagerHeight) {
            val spacingInPx = itemSpacing.roundToPx()

            placeables.forEachIndexed { index, placeable ->
                val xCenterOffset = horizontalAlignment.align(
                    size = placeable.width,
                    space = pagerWidth,
                    layoutDirection = layoutDirection,
                )
                val yCenterOffset = verticalAlignment.align(
                    size = placeable.height,
                    space = pagerHeight,
                )

                val offsetForPage = index - 1 - offset
                val xItemOffset = (offsetForPage * (placeable.width + spacingInPx)).roundToInt()

                // We can't rely on placeRelative() since that only uses the LayoutDirection, and
                // we need to cater for our reverseLayout param too. reverseDirection contains
                // the resolved direction, so we use that to flip the offset direction...
                placeable.place(
                    x = xCenterOffset + if (reverseDirection) -xItemOffset else xItemOffset,
                    y = yCenterOffset,
                )
            }
        }
    }
}

private const val SNAP_SPRING_STIFFNESS = 2750f

@Composable
fun <T> rememberPagerFlingConfig(
    state: InfiniteViewPagerState<T>,
    snapAnimationSpec: AnimationSpec<Float> = spring(stiffness = SNAP_SPRING_STIFFNESS),
): FlingBehavior = remember(state, snapAnimationSpec) {
    object : FlingBehavior {
        override suspend fun ScrollScope.performFling(
            initialVelocity: Float
        ): Float = state.fling(
            initialVelocity = -initialVelocity,
            snapAnimationSpec = snapAnimationSpec,
            scrollBy = { deltaPixels -> -scrollBy(-deltaPixels) },
        )
    }
}

private object ConsumeFlingNestedScrollConnection : NestedScrollConnection {
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset = when (source) {
        // We can consume all resting fling scrolls so that they don't propagate up to the
        // Pager
        NestedScrollSource.Fling -> available
        else -> Offset.Zero
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        // We can consume all post fling velocity so that it doesn't propagate up to the Pager
        return available
    }
}

@Stable
interface InfinitePagerScope<T> : BoxScope {
    /**
     * Returns the current selected page offset
     */
    val currentPageOffset: Float
}

private class InfinitePagerScopeImpl<T>(
    private val boxScope: BoxScope,
    private val state: InfiniteViewPagerState<T>,
) : InfinitePagerScope<T>, BoxScope by boxScope {
    override val currentPageOffset: Float get() = state.currentLayoutPageOffset
}

@Immutable
private data class PageData<T>(val page: T) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@PageData
}
