package com.skyyo.samples.features.navigateWithResult.simple.dogDetails

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel

private const val ANIMATION_DURATION = 1500

@Composable
fun DogDetailsScreen(viewModel: DogDetailsViewModel = hiltViewModel()) {
    Box(Modifier.fillMaxSize().background(Color.Magenta)) {
        Button(viewModel::goContacts, Modifier.statusBarsPadding()) {
            Text("Dog Details: ${viewModel.dogId}  go contacts!")
        }
    }
}

fun dogDetailsEnterTransition(startAnimationRect: Rect, endAnimationRect: Rect): EnterTransition {
    val expandTransition = expandIn(
        expandFrom = Alignment.TopStart,
        animationSpec = tween(durationMillis = ANIMATION_DURATION),
        initialSize = {
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

private const val EXIT_ANIMATION_ROUNDING_ERROR = 10e-3f

fun dogDetailsExitTransition(startAnimationRect: Rect, endAnimationRect: Rect): ExitTransition {
    val shrinkTransition = shrinkOut(
        shrinkTowards = Alignment.TopStart,
        animationSpec = tween(ANIMATION_DURATION),
        targetSize = { IntSize(endAnimationRect.width.toInt(), endAnimationRect.height.toInt()) }
    )

    val slideOutTransition = slideOut(
        animationSpec = tween(ANIMATION_DURATION),
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

@Suppress("MagicNumber")
fun dogDetailsPreviousDestinationPopEnterTransition(): EnterTransition {
    return fadeIn(animationSpec = tween(durationMillis = 500, delayMillis = ANIMATION_DURATION - 300))
}
