package com.skyyo.samples.features.autoComplete

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import kotlin.math.max
import kotlin.math.min

private val MENU_VERTICAL_MARGIN = 48.dp
private val MENU_ELEVATION = 8.dp
private const val IN_TRANSITION_DURATION = 120
private const val OUT_TRANSITION_DURATION = 75

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Dropdown(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    anchor: @Composable () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = false,
        onExpandedChange = { }
    ) {
        anchor()
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
        ) {
            content(Modifier.exposedDropdownSize().clip(RoundedCornerShape(8.dp)))
        }
    }
}

@Composable
private fun DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    val expandedStates = remember { MutableTransitionState(false) }
    expandedStates.targetState = expanded

    if (expandedStates.currentState || expandedStates.targetState) {
        var contentTransformOrigin by remember { mutableStateOf(TransformOrigin.Center) }
        val density = LocalDensity.current
        val popupPositionProvider = DropdownMenuPositionProvider(
            contentOffset = DpOffset(0.dp, 8.dp),
            density = density
        ) { anchorBounds, popupBounds ->
            contentTransformOrigin = calculateTransformOriginNew(anchorBounds, popupBounds)
        }

        Popup(
            onDismissRequest = onDismissRequest,
            popupPositionProvider = popupPositionProvider,
            properties = PopupProperties()
        ) {
            DropdownMenuContent(
                expandedStates = expandedStates,
                contentTransformOrigin = contentTransformOrigin,
                content = content
            )
        }
    }
}

@Composable
private fun DropdownMenuContent(
    expandedStates: MutableTransitionState<Boolean>,
    contentTransformOrigin: TransformOrigin,
    content: @Composable () -> Unit
) {
    // Menu open/close animation.
    val transition = updateTransition(expandedStates, "DropDownMenu")

    val contentScale by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                // Dismissed to expanded
                tween(durationMillis = IN_TRANSITION_DURATION, easing = LinearOutSlowInEasing)
            } else {
                // Expanded to dismissed.
                tween(durationMillis = 1, delayMillis = OUT_TRANSITION_DURATION - 1)
            }
        },
        label = "dropdownContentScale"
    ) { expanded -> if (expanded) 1f else 0.8f }

    val contentAlpha by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                // Dismissed to expanded
                tween(durationMillis = 30)
            } else {
                // Expanded to dismissed.
                tween(durationMillis = OUT_TRANSITION_DURATION)
            }
        },
        label = "dropdownContentAlpha"
    ) { expanded -> if (expanded) 1f else 0f }

    Card(
        modifier = Modifier
            .graphicsLayer {
                scaleX = contentScale
                scaleY = contentScale
                alpha = contentAlpha
                transformOrigin = contentTransformOrigin
            },
        elevation = MENU_ELEVATION
    ) {
        content()
    }
}

private fun calculateTransformOriginNew(
    anchorBounds: IntRect,
    popupBounds: IntRect
): TransformOrigin {
    val pivotX = when {
        popupBounds.left >= anchorBounds.right -> 0f
        popupBounds.right <= anchorBounds.left -> 1f
        popupBounds.width == 0 -> 0f
        else -> {
            val intersectionCenter =
                (
                    max(anchorBounds.left, popupBounds.left) +
                        min(anchorBounds.right, popupBounds.right)
                    ) / 2
            (intersectionCenter - popupBounds.left).toFloat() / popupBounds.width
        }
    }
    val pivotY = when {
        popupBounds.top >= anchorBounds.bottom -> 0f
        popupBounds.bottom <= anchorBounds.top -> 1f
        popupBounds.height == 0 -> 0f
        else -> {
            val intersectionCenter =
                (
                    max(anchorBounds.top, popupBounds.top) +
                        min(anchorBounds.bottom, popupBounds.bottom)
                    ) / 2
            (intersectionCenter - popupBounds.top).toFloat() / popupBounds.height
        }
    }
    return TransformOrigin(pivotX, pivotY)
}

@Immutable
private data class DropdownMenuPositionProvider(
    val contentOffset: DpOffset,
    val density: Density,
    val onPositionCalculated: (anchorBounds: IntRect, popupBounds: IntRect) -> Unit = { _, _ -> }
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        // The min margin above and below the menu, relative to the screen.
        val verticalMargin = with(density) { MENU_VERTICAL_MARGIN.roundToPx() }
        // The content offset specified using the dropdown offset parameter.
        val contentOffsetX = with(density) { contentOffset.x.roundToPx() }
        val contentOffsetY = with(density) { contentOffset.y.roundToPx() }

        // Compute horizontal position.
        val toRight = anchorBounds.left + contentOffsetX
        val toLeft = anchorBounds.right - contentOffsetX - popupContentSize.width
        val toDisplayRight = windowSize.width - popupContentSize.width
        val toDisplayLeft = 0
        val x = if (layoutDirection == LayoutDirection.Ltr) {
            sequenceOf(
                toRight,
                toLeft,
                // If the anchor gets outside of the window on the left, we want to position
                // toDisplayLeft for proximity to the anchor. Otherwise, toDisplayRight.
                if (anchorBounds.left >= 0) toDisplayRight else toDisplayLeft
            )
        } else {
            sequenceOf(
                toLeft,
                toRight,
                // If the anchor gets outside of the window on the right, we want to position
                // toDisplayRight for proximity to the anchor. Otherwise, toDisplayLeft.
                if (anchorBounds.right <= windowSize.width) toDisplayLeft else toDisplayRight
            )
        }.firstOrNull {
            it >= 0 && it + popupContentSize.width <= windowSize.width
        } ?: toLeft

        val heightAboveAnchor = anchorBounds.top - contentOffsetY - verticalMargin
        val heightBelowAnchor = windowSize.height - verticalMargin - anchorBounds.bottom - contentOffsetY
        val y: Int
        val height: Int

        when {
            // anchor is too big to fit into window with verticalMargin step below and above popup
            heightAboveAnchor < 0 && heightBelowAnchor < 0 -> {
                height = popupContentSize.height
                y = anchorBounds.bottom + contentOffsetY
            }
            heightAboveAnchor > heightBelowAnchor -> {
                height = minOf(heightAboveAnchor, popupContentSize.height)
                y = anchorBounds.top - contentOffsetY - height
            }
            else -> {
                height = minOf(heightBelowAnchor, popupContentSize.height)
                y = anchorBounds.bottom + contentOffsetY
            }
        }

        onPositionCalculated(
            anchorBounds,
            IntRect(x, y, x + popupContentSize.width, y + height)
        )

        return IntOffset(x, y)
    }
}
