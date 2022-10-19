package com.skyyo.samples.features.navigateWithResult.simple.dogDetails

import android.os.SystemClock
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.lerp

private const val ANIMATION_DURATION = 1500
private const val FADE_PREVIOUS_DESTINATION_DURATION = 300
private const val EXIT_ANIMATION_ROUNDING_ERROR = 10e-3f

class DogDetailsTransitionsProvider {
    private var startPlayingTime = 0L

    fun getEnterTransition(startAnimationRect: Rect, endAnimationRect: Rect): EnterTransition {
        startPlayingTime = 0L
        val expandTransition = expandIn(
            expandFrom = Alignment.TopStart,
            animationSpec = tween(durationMillis = ANIMATION_DURATION),
            initialSize = {
                if (startPlayingTime == 0L) startPlayingTime = SystemClock.uptimeMillis()
                IntSize(startAnimationRect.width.toInt(), startAnimationRect.height.toInt())
            }
        )
        val slideInTransition = slideIn(
            animationSpec = tween(durationMillis = ANIMATION_DURATION),
            initialOffset = {
                val xOffset =
                    startAnimationRect.bottomRight.x - it.width / 2 - endAnimationRect.width / 2
                val yOffset =
                    startAnimationRect.bottomRight.y - it.height / 2 - endAnimationRect.height / 2
                IntOffset(
                    x = xOffset.toInt(),
                    y = yOffset.toInt()
                )
            }
        )
        return slideInTransition + expandTransition
    }

    fun getExitTransition(startAnimationRect: Rect, endAnimationRect: Rect): ExitTransition {
        val easing = FastOutSlowInEasing
        val shrinkTransition = shrinkOut(
            shrinkTowards = Alignment.TopStart,
            animationSpec = tween(durationMillis = ANIMATION_DURATION, easing = easing),
            targetSize = { IntSize(endAnimationRect.width.toInt(), endAnimationRect.height.toInt()) }
        )

        val slideOutTransition = slideOut(
            animationSpec = tween(durationMillis = ANIMATION_DURATION, easing = easing),
            targetOffset = {
                val fractionX = minOf(1f - (it.width - endAnimationRect.width) / (startAnimationRect.width - endAnimationRect.width), 1f - EXIT_ANIMATION_ROUNDING_ERROR)
                val fractionY = minOf(1f - (it.height - endAnimationRect.height) / (startAnimationRect.height - endAnimationRect.height), 1f - EXIT_ANIMATION_ROUNDING_ERROR)
                val xOffset = (lerp(0f, endAnimationRect.left, fractionX) - startAnimationRect.width / 2).toInt() + it.width / 2
                val yOffset = (lerp(0f, endAnimationRect.top, fractionY) - startAnimationRect.height / 2).toInt() + it.height / 2
                IntOffset(
                    x = xOffset,
                    y = yOffset
                )
            }
        )

        return shrinkTransition + slideOutTransition
    }

    fun getPreviousDestinationPopEnterTransition(): EnterTransition {
        return when {
            SystemClock.uptimeMillis() - startPlayingTime < ANIMATION_DURATION -> {
                fadeIn(animationSpec = tween(FADE_PREVIOUS_DESTINATION_DURATION))
            }
            else -> fadeIn(
                animationSpec = tween(
                    durationMillis = FADE_PREVIOUS_DESTINATION_DURATION,
                    delayMillis = ANIMATION_DURATION - FADE_PREVIOUS_DESTINATION_DURATION
                )
            )
        }
    }
}
