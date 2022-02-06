package com.skyyo.samples.features.snap

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.SnapperLayoutInfo
import dev.chrisbanes.snapper.SnapperLayoutItemInfo
import kotlin.math.abs
import kotlin.math.absoluteValue

/**
 * A snapping [FlingBehavior] for [LazyListState]. Typically this would be created
 * via [rememberSnapperFlingBehavior].
 *
 * Note: the default parameter value for [decayAnimationSpec] is different to the value used in
 * [rememberSnapperFlingBehavior], due to not being able to access composable functions.
 *
 * @param layoutInfo The [SnapperLayoutInfo] to use.
 * @param decayAnimationSpec The decay animation spec to use for decayed flings.
 * @param springAnimationSpec The animation spec to use when snapping.
 * @param maximumFlingDistance Block which returns the maximum fling distance in pixels.
 * The returned value should be > 0.
 */
@ExperimentalSnapperApi
class SnapperFlingBehavior(
    private val snapItemsCount: Int,
    private val layoutInfo: SnapperLayoutInfo,
    private val maximumFlingDistance: (SnapperLayoutInfo) -> Float = { Float.MAX_VALUE },
    private val decayAnimationSpec: DecayAnimationSpec<Float>,
    private val springAnimationSpec: AnimationSpec<Float> = spring(stiffness = 400f),
) : FlingBehavior {
    /**
     * The target item index for any on-going animations.
     */
    var animationTarget: Int? by mutableStateOf(null)
        private set

    override suspend fun ScrollScope.performFling(
        initialVelocity: Float
    ): Float {
        // If we're at the start/end of the scroll range, we don't snap and assume the user
        // wanted to scroll here.
        if (!layoutInfo.canScrollTowardsStart() || !layoutInfo.canScrollTowardsEnd()) {
            return initialVelocity
        }

        Log.d("SnapperFlinger", "performFling. initialVelocity: $initialVelocity" )

        val maxFlingDistance = maximumFlingDistance(layoutInfo)
        require(maxFlingDistance > 0) {
            "Distance returned by maximumFlingDistance should be greater than 0"
        }

        return flingToIndex(
            index = layoutInfo.determineTargetIndex(
                velocity = initialVelocity,
                decayAnimationSpec = decayAnimationSpec,
                maximumFlingDistance = maxFlingDistance,
            ),
            initialVelocity = initialVelocity,
        )
    }

    private suspend fun ScrollScope.flingToIndex(
        index: Int,
        initialVelocity: Float,
    ): Float {
        val initialItem = layoutInfo.currentItem ?: return initialVelocity

        if (initialItem.index == index && layoutInfo.distanceToIndexSnap(initialItem.index) == 0) {
            Log.d("SnapperFlinger",
                "flingToIndex. Skipping fling, already at target. " +
                        "vel:$initialVelocity, " +
                        "initial item: $initialItem, " +
                        "target: $index"
            )
            return consumeVelocityIfNotAtScrollEdge(initialVelocity)
        }

        return if (decayAnimationSpec.canDecayBeyondCurrentItem(initialVelocity, initialItem)) {
            // If the decay fling can scroll past the current item, fling with decay
            performDecayFling(
                initialItem = initialItem,
                targetIndex = index,
                initialVelocity = initialVelocity,
            )
        } else {
            // Otherwise we 'spring' to current/next item
            performSpringFling(
                initialItem = initialItem,
                targetIndex = index,
                initialVelocity = initialVelocity,
            )
        }
    }

    /**
     * Performs a decaying fling.
     *
     * If [flingThenSpring] is set to true, then a fling-then-spring animation might be used.
     * If used, a decay fling will be run until we've scrolled to the preceding item of
     * [targetIndex]. Once that happens, the decay animation is stopped and a spring animation
     * is started to scroll the remainder of the distance. Visually this results in a much
     * smoother finish to the animation, as it will slowly come to a stop at [targetIndex].
     * Even if [flingThenSpring] is set to true, fling-then-spring animations are only available
     * when scrolling 2 items or more.
     *
     * When [flingThenSpring] is not used, the decay animation will be stopped immediately upon
     * scrolling past [targetIndex], which can result in an abrupt stop.
     */
    private suspend fun ScrollScope.performDecayFling(
        initialItem: SnapperLayoutItemInfo,
        targetIndex: Int,
        initialVelocity: Float,
        flingThenSpring: Boolean = false,
    ): Float {
        // If we're already at the target + snap offset, skip
        if (initialItem.index == targetIndex && layoutInfo.distanceToIndexSnap(initialItem.index) == 0) {
            Log.d("SnapperFlinger",
                "performDecayFling. Skipping decay, already at target. " +
                        "vel:$initialVelocity, " +
                        "current item: $initialItem, " +
                        "target: $targetIndex"
            )
            return consumeVelocityIfNotAtScrollEdge(initialVelocity)
        }

        Log.d("SnapperFlinger",
            "Performing decay fling. " +
                    "vel:$initialVelocity, " +
                    "current item: $initialItem, " +
                    "target: $targetIndex"
)

        var velocityLeft = initialVelocity
        var lastValue = 0f

        // We can only fling-then-spring if we're flinging >= 2 items...
        val canSpringThenFling = flingThenSpring && abs(targetIndex - initialItem.index) >= 2
        var needSpringAfter = false

        try {
            // Update the animationTarget
            animationTarget = targetIndex

            AnimationState(
                initialValue = 0f,
                initialVelocity = initialVelocity,
            ).animateDecay(decayAnimationSpec) {
                val delta = value - lastValue
                val consumed = scrollBy(delta)
                lastValue = value
                velocityLeft = velocity

                if (abs(delta - consumed) > 0.5f) {
                    // If some of the scroll was not consumed, cancel the animation now as we're
                    // likely at the end of the scroll range
                    cancelAnimation()
                }

                val currentItem = layoutInfo.currentItem
                if (currentItem == null) {
                    cancelAnimation()
                    return@animateDecay
                }

                if (isRunning && canSpringThenFling) {
                    // If we're still running and fling-then-spring is enabled, check to see
                    // if we're at the 1 item width away (in the relevant direction). If we are,
                    // set the spring-after flag and cancel the current decay
                    if (velocity > 0 && currentItem.index == targetIndex - 1) {
                        needSpringAfter = true
                        cancelAnimation()
                    } else if (velocity < 0 && currentItem.index == targetIndex) {
                        needSpringAfter = true
                        cancelAnimation()
                    }
                }

                if (isRunning && performSnapBackIfNeeded(currentItem, targetIndex, ::scrollBy)) {
                    // If we're still running, check to see if we need to snap-back
                    // (if we've scrolled past the target)
                    cancelAnimation()
                }
            }
        } finally {
            animationTarget = null
        }

        Log.d("SnapperFlinger",
            "Decay fling finished. Distance: $lastValue. Final vel: $velocityLeft"
)

        if (!needSpringAfter && layoutInfo.currentItem?.let { it.index % snapItemsCount == 0  && it.offset == 0} == false) {
            needSpringAfter = true
        }

        if (needSpringAfter) {
            // The needSpringAfter flag is enabled, so start a spring to the target using the
            // remaining velocity
            return performSpringFling(layoutInfo.currentItem!!, targetIndex, velocityLeft)
        }

        return consumeVelocityIfNotAtScrollEdge(velocityLeft)
    }

    private suspend fun ScrollScope.performSpringFling(
        initialItem: SnapperLayoutItemInfo,
        targetIndex: Int,
        initialVelocity: Float = 0f,
    ): Float {
        Log.d("SnapperFlinger",
            "performSpringFling. " +
                    "vel:$initialVelocity, " +
                    "initial item: $initialItem, " +
                    "target: $targetIndex"
        )

        var velocityLeft = when {
            // Only use the initialVelocity if it is in the correct direction
            targetIndex > initialItem.index && initialVelocity > 0 -> initialVelocity
            targetIndex <= initialItem.index && initialVelocity < 0 -> initialVelocity
            // Otherwise start at 0 velocity
            else -> 0f
        }
        var lastValue = 0f

        try {
            // Update the animationTarget
            animationTarget = targetIndex

            AnimationState(
                initialValue = lastValue,
                initialVelocity = velocityLeft,
            ).animateTo(
                targetValue = layoutInfo.distanceToIndexSnap(targetIndex).toFloat(),
                animationSpec = springAnimationSpec,
            ) {
                val delta = value - lastValue
                val consumed = scrollBy(delta)
                lastValue = value
                velocityLeft = velocity

                val currentItem = layoutInfo.currentItem
                if (currentItem == null) {
                    cancelAnimation()
                    return@animateTo
                }

                if (performSnapBackIfNeeded(currentItem, targetIndex, ::scrollBy)) {
                    cancelAnimation()
                } else if (abs(delta - consumed) > 0.5f) {
                    // If we're still running but some of the scroll was not consumed,
                    // cancel the animation now
                    cancelAnimation()
                }
            }
        } finally {
            animationTarget = null
        }

        Log.d("SnapperFlinger",
            "Spring fling finished. Distance: $lastValue. Final vel: $velocityLeft"
)

        return consumeVelocityIfNotAtScrollEdge(velocityLeft)
    }

    /**
     * Returns true if we needed to perform a snap back, and the animation should be cancelled.
     */
    private fun AnimationScope<Float, AnimationVector1D>.performSnapBackIfNeeded(
        currentItem: SnapperLayoutItemInfo,
        targetIndex: Int,
        scrollBy: (pixels: Float) -> Float,
    ): Boolean {
        Log.d("SnapperFlinger",
            "scroll tick. " +
                    "vel:$velocity, " +
                    "current item: $currentItem"
        )

        // Calculate the 'snap back'. If the returned value is 0, we don't need to do anything.
        val snapBackAmount = calculateSnapBack(velocity, currentItem, targetIndex)

        if (snapBackAmount != 0) {
            // If we've scrolled to/past the item, stop the animation. We may also need to
            // 'snap back' to the item as we may have scrolled past it
            Log.d("SnapperFlinger",
                "Scrolled past item. " +
                        "vel:$velocity, " +
                        "current item: $currentItem} " +
                        "target:$targetIndex"
            )
            scrollBy(snapBackAmount.toFloat())
            return true
        }

        return false
    }

    private fun DecayAnimationSpec<Float>.canDecayBeyondCurrentItem(
        velocity: Float,
        currentItem: SnapperLayoutItemInfo,
    ): Boolean {
        // If we don't have a velocity, return false
        if (velocity.absoluteValue < 0.5f) return false

        val flingDistance = calculateTargetValue(0f, velocity)

        Log.d("SnapperFlinger",
            "canDecayBeyondCurrentItem. " +
                    "initialVelocity: $velocity, " +
                    "flingDistance: $flingDistance, " +
                    "current item: $currentItem"
        )

        return if (velocity < 0) {
            // backwards, towards 0
            flingDistance <= layoutInfo.distanceToIndexSnap(currentItem.index)
        } else {
            // forwards, toward index + 1
            flingDistance >= layoutInfo.distanceToIndexSnap(currentItem.index + 1)
        }
    }

    /**
     * Returns the distance in pixels that is required to 'snap back' to the [targetIndex].
     * Returns 0 if a snap back is not needed.
     */
    private fun calculateSnapBack(
        initialVelocity: Float,
        currentItem: SnapperLayoutItemInfo,
        targetIndex: Int
    ): Int = 0

    private fun consumeVelocityIfNotAtScrollEdge(velocity: Float): Float {
        if (velocity < 0 && !layoutInfo.canScrollTowardsStart()) {
            // If there is remaining velocity towards the start and we're at the scroll start,
            // we don't consume. This enables the overscroll effect where supported
            return velocity
        } else if (velocity > 0 && !layoutInfo.canScrollTowardsEnd()) {
            // If there is remaining velocity towards the end and we're at the scroll end,
            // we don't consume. This enables the overscroll effect where supported
            return velocity
        }
        // Else we return 0 to consume the remaining velocity
        return 0f
    }
}