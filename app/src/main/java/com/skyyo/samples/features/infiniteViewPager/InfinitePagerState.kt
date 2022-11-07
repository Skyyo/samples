package com.skyyo.samples.features.infiniteViewPager

import androidx.compose.animation.core.*
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*

private const val INDEX_OF_CURRENT_PAGE_IN_LAYOUT_PAGES = 1

@Stable
class InfinitePagerState<T>(
    private val circularListIterator: CircularListIterator<T>
) : ScrollableState {
    var currentLayoutPageOffset by mutableStateOf(0f)
    val layoutPages = mutableStateListOf(peekByIndex(0), peekByIndex(1), peekByIndex(2))

    private fun peekByIndex(index: Int) = when (index) {
        0 -> circularListIterator.peekPrevious()
        1 -> circularListIterator.peekCurrent()
        else -> circularListIterator.peekNext()
    }

    var layoutSize: Int by mutableStateOf(0)

    /**
     * The ScrollableController instance. We keep it as we need to call stopAnimation on it once
     * we reached the end of the list.
     */
    private val scrollableState = ScrollableState { deltaPixels ->
        // scrollByOffset expects values in an opposite sign to what we're passed, so we need
        // to negate the value passed in, and the value returned.
        val size = layoutSize
        require(size > 0) { "Layout size for current item is 0" }
        scrollByOffset(-deltaPixels / size) * size
    }

    /**
     * [InteractionSource] that will be used to dispatch drag events when this
     * list is being dragged. If you want to know whether the fling (or animated scroll) is in
     * progress, use [isScrollInProgress].
     */
    val interactionSource: InteractionSource
        get() = internalInteractionSource

    internal val internalInteractionSource: MutableInteractionSource = MutableInteractionSource()

    private fun snapToPage(next: Boolean, offset: Float = 0f) {
        // Snap the layout
        setCurrentPageOffset(offset)
        // Then update the current page to match
        setCurrentPage(next)
    }

    private fun setCurrentPageOffset(offset: Float) {
        currentLayoutPageOffset = offset.coerceIn(minimumValue = -1f, maximumValue = 1f)
    }

    private fun setCurrentPage(next: Boolean) {
        if (next) circularListIterator.moveNext() else circularListIterator.movePrevious()
        for (i in layoutPages.indices) {
            layoutPages[i] = peekByIndex(i)
        }
    }

    /**
     * Updates the [layoutPages] so that for the given [scrollPercent].
     */
    private fun updateLayoutForScrollPosition(scrollPercent: Float) {
        setCurrentPageOffset(scrollPercent - 1)
    }

    /**
     * Scroll by the pager with the given [deltaInPercents].
     *
     * @param deltaInPercents delta in offset values (0f..1f). Values > 0 signify scrolls
     * towards the end of the pager, and values < 0 towards the start.
     * @return the amount of [deltaInPercents] consumed
     */
    private fun scrollByOffset(deltaInPercents: Float): Float {
        val current = INDEX_OF_CURRENT_PAGE_IN_LAYOUT_PAGES + currentLayoutPageOffset
        val target = current + deltaInPercents
        updateLayoutForScrollPosition(target)
        return current - target
    }

    /**
     * Fling the pager with the given [initialVelocity]. [scrollBy] will called whenever a
     * scroll change is required by the fling.
     *
     * @param initialVelocity velocity in pixels per second. Values > 0 signify flings
     * towards the end of the pager, and values < 0 sign flings towards the start.
     * @param snapAnimationSpec The animation spec to use when snapping.
     * @param scrollBy block which is called when a scroll is required. Positive values passed in
     * signify scrolls towards the end of the pager, and values < 0 towards the start.
     * @return any remaining velocity after the scroll has finished.
     */
    internal suspend fun fling(
        initialVelocity: Float,
        snapAnimationSpec: AnimationSpec<Float> = spring(),
        scrollBy: (Float) -> Float,
    ): Float {
        if (currentLayoutPageOffset == 0f) return initialVelocity
        var lastVelocity: Float = initialVelocity
        val next = currentLayoutPageOffset > 0
        val targetValue = when {
            next -> layoutSize.toFloat()
            else -> -layoutSize.toFloat()
        }
        animate(
            initialValue = currentLayoutPageOffset * layoutSize,
            targetValue = targetValue,
            initialVelocity = initialVelocity,
            animationSpec = snapAnimationSpec,
        ) { value, velocity ->
            scrollBy(value - currentLayoutPageOffset * layoutSize)
            // Keep track of velocity
            lastVelocity = velocity
        }
        snapToPage(next)
        return lastVelocity
    }

    override val isScrollInProgress: Boolean
        get() = scrollableState.isScrollInProgress

    override fun dispatchRawDelta(delta: Float): Float {
        return scrollableState.dispatchRawDelta(delta)
    }

    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ) {
        scrollableState.scroll(scrollPriority, block)
        when (currentLayoutPageOffset) {
            1f -> snapToPage(next = true)
            -1f -> snapToPage(next = false)
        }
    }

    override fun toString(): String = "PagerState(offset=$currentLayoutPageOffset)"
}
