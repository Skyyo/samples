package com.skyyo.samples.features.snap

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.snapper.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Create and remember a snapping [FlingBehavior] to be used with the given [layoutInfo].
 *
 * @param layoutInfo The [SnapperLayoutInfo] to use. For lazy layouts,
 * you can use [rememberLazyListSnapperLayoutInfo].
 * @param decayAnimationSpec The decay animation spec to use for decayed flings.
 * @param springAnimationSpec The animation spec to use when snapping.
 * @param maximumFlingDistance Block which returns the maximum fling distance in pixels.
 * The returned value should be > 0.
 */
@ExperimentalSnapperApi
@Composable
fun rememberSnapperFlingBehavior(
    snapItemsCount: Int,
    layoutInfo: SnapperLayoutInfo,
    decayAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
    springAnimationSpec: AnimationSpec<Float> = SnapperFlingBehaviorDefaults.SpringAnimationSpec,
    maximumFlingDistance: (SnapperLayoutInfo) -> Float = SnapperFlingBehaviorDefaults.MaximumFlingDistance,
): SnapperFlingBehavior = remember(
    layoutInfo,
    decayAnimationSpec,
    springAnimationSpec,
    maximumFlingDistance,
) {
    SnapperFlingBehavior(
        snapItemsCount = snapItemsCount,
        layoutInfo = layoutInfo,
        decayAnimationSpec = decayAnimationSpec,
        springAnimationSpec = springAnimationSpec,
        maximumFlingDistance = maximumFlingDistance,
    )
}

/**
 * Create and remember a [SnapperLayoutInfo] which works with [LazyListState].
 *
 * @param lazyListState The [LazyListState] to update.
 * @param snapOffsetForItem Block which returns which offset the given item should 'snap' to.
 * See [SnapOffsets] for provided values.
 * @param endContentPadding The amount of content padding on the end edge of the lazy list
 * in dps (end/bottom depending on the scrolling direction).
 */
@ExperimentalSnapperApi
@Composable
fun rememberLazyListSnapperLayoutInfo(
    lazyListState: LazyListState,
    snapItemsCount: Int,
    endContentPadding: Dp = 0.dp,
): SnapperLayoutInfo = remember(lazyListState, snapItemsCount) {
    LazyListNSnapperLayoutInfo(
        lazyListState = lazyListState,
        snapItemsCount = snapItemsCount
    )
}.apply {
    this.endContentPadding = with(LocalDensity.current) { endContentPadding.roundToPx() }
}

/**
 * A [SnapperLayoutInfo] which works with [LazyListState]. Typically this would be remembered
 * using [rememberLazyListSnapperLayoutInfo].
 *
 * @param lazyListState The [LazyListState] to update.
 * @param snapOffsetForItem Block which returns which offset the given item should 'snap' to.
 * See [SnapOffsets] for provided values.
 * @param endContentPadding The amount of content padding on the end edge of the lazy list
 * in pixels (end/bottom depending on the scrolling direction).
 */
@ExperimentalSnapperApi
class LazyListNSnapperLayoutInfo(
    private val snapItemsCount: Int,
    private val lazyListState: LazyListState,
    endContentPadding: Int = 0,
) : SnapperLayoutInfo() {

    private fun Int.minIndexToSnap(): Int {
        var indexToSnap = -1
        var firstItem: SnapperLayoutItemInfo? = null
        for (visibleItem in visibleItems.asIterable()) {
            if (firstItem == null) firstItem = visibleItem
            if (visibleItem.index % snapItemsCount == 0) {
                indexToSnap = visibleItem.index
                break
            }
        }
        indexToSnap = when {
            indexToSnap == -1 -> (this / snapItemsCount) * snapItemsCount
            indexToSnap != firstItem?.index -> indexToSnap - snapItemsCount
            else -> indexToSnap
        }

        return indexToSnap
    }

    override val startScrollOffset: Int = 0

    internal var endContentPadding: Int by mutableStateOf(endContentPadding)

    override val endScrollOffset: Int
        get() = lazyListState.layoutInfo.viewportEndOffset - endContentPadding

    private val itemCount: Int get() = lazyListState.layoutInfo.totalItemsCount

    override val currentItem: SnapperLayoutItemInfo?
        get() = visibleItems.lastOrNull { it.offset <= 0 }

    override val visibleItems: Sequence<SnapperLayoutItemInfo>
        get() = lazyListState.layoutInfo.visibleItemsInfo.asSequence()
            .map(::LazyListSnapperLayoutItemInfo)

    override fun distanceToIndexSnap(index: Int): Int {
        // Otherwise we need to guesstimate, using the current item snap point and
        // multiplying distancePerItem by the index delta
        val currentItem = currentItem ?: return 0 // TODO: throw?
        return ((index - currentItem.index) * estimateDistancePerItem()).roundToInt() +
                currentItem.offset
    }

    override fun canScrollTowardsStart(): Boolean {
        return lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()?.let {
            it.index > 0 || it.offset < startScrollOffset
        } ?: false
    }

    override fun canScrollTowardsEnd(): Boolean {
        return lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.let {
            it.index < itemCount - 1 || (it.offset + it.size) > endScrollOffset
        } ?: false
    }

    override fun determineTargetIndex(
        velocity: Float,
        decayAnimationSpec: DecayAnimationSpec<Float>,
        maximumFlingDistance: Float,
    ): Int {
        val curr = currentItem ?: return -1
        val currentIndex = curr.index
        val offset = curr.offset
        val indexToSnap = currentIndex.minIndexToSnap()

        val distancePerItem = estimateDistancePerItem()
        if (distancePerItem <= 0) {
            // If we don't have a valid distance, return the current item
            return indexToSnap
        }

        val flingDistance = decayAnimationSpec.calculateTargetValue(0f, velocity)
            .coerceIn(-maximumFlingDistance, maximumFlingDistance)

        val distanceNext = (indexToSnap + snapItemsCount - currentIndex) * distancePerItem
        val distanceCurrent = (currentIndex - indexToSnap) * distancePerItem

        // If the fling doesn't reach the next snap point (in the fling direction), we try
        // and snap depending on which snap point is closer to the current scroll position
        if (
            (flingDistance >= 0 && flingDistance < distanceNext / 2) ||
            (flingDistance < 0 && -flingDistance < distanceCurrent / 2)
        ) {
            return if (distanceNext + curr.offset < distanceCurrent - curr.offset) {
                (indexToSnap + snapItemsCount).coerceIn(0, (itemCount / snapItemsCount) * snapItemsCount)
            } else {
                indexToSnap
            }
        }

        return if (flingDistance < 0) {
            val shifting = (flingDistance + (currentIndex - indexToSnap - snapItemsCount) * distancePerItem - offset) / distancePerItem
            var targetIndex = (shifting.toInt() / snapItemsCount) * snapItemsCount
            val snapOffset = shifting - targetIndex
            if (snapOffset > -snapItemsCount / 2f) targetIndex += snapItemsCount
            indexToSnap + targetIndex
        } else {
            val shifting = ((currentIndex - indexToSnap) * distancePerItem + flingDistance - offset) / distancePerItem
            var targetIndex = (shifting.toInt() / snapItemsCount) * snapItemsCount
            val snapOffset = shifting - targetIndex
            if (snapOffset > snapItemsCount / 2f) targetIndex += snapItemsCount
            indexToSnap + targetIndex
        }
    }

    /**
     * This attempts to calculate the item spacing for the layout, by looking at the distance
     * between the visible items. If there's only 1 visible item available, it returns 0.
     */
    private fun calculateItemSpacing(): Int = with(lazyListState.layoutInfo) {
        if (visibleItemsInfo.size >= 2) {
            val first = visibleItemsInfo[0]
            val second = visibleItemsInfo[1]
            second.offset - (first.size + first.offset)
        } else 0
    }

    /**
     * Computes an average pixel value to pass a single child.
     *
     * Returns a negative value if it cannot be calculated.
     *
     * @return A float value that is the average number of pixels needed to scroll by one view in
     * the relevant direction.
     */
    private fun estimateDistancePerItem(): Float = with(lazyListState.layoutInfo) {
        if (visibleItemsInfo.isEmpty()) return -1f

        val minPosView = visibleItemsInfo.minByOrNull { it.offset } ?: return -1f
        val maxPosView = visibleItemsInfo.maxByOrNull { it.offset + it.size } ?: return -1f

        val start = min(minPosView.offset, maxPosView.offset)
        val end = max(minPosView.offset + minPosView.size, maxPosView.offset + maxPosView.size)

        // We add an extra `itemSpacing` onto the calculated total distance. This ensures that
        // the calculated mean contains an item spacing for each visible item
        // (not just spacing between items)
        return when (val distance = end - start) {
            0 -> -1f // If we don't have a distance, return -1
            else -> (distance + calculateItemSpacing()) / visibleItemsInfo.size.toFloat()
        }
    }

    private class LazyListSnapperLayoutItemInfo(
        private val lazyListItem: LazyListItemInfo,
    ) : SnapperLayoutItemInfo() {
        override val index: Int get() = lazyListItem.index
        override val offset: Int get() = lazyListItem.offset
        override val size: Int get() = lazyListItem.size
    }
}