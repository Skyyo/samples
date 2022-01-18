package com.skyyo.samples.features.marqueeText

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.SubcomposeMeasureScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

enum class ScrollDirection { Forward, Backward }
const val SPACING_NONE = 0
const val SPACING_LARGE = 1
const val SPACING_MEDIUM = 2
const val SPACING_SMALL = 3

private enum class MarqueeComponents { MainText, NextText, EdgesGradient }
private data class TextLayoutInfo(val textWidth: Int, val containerWidth: Int)

@Composable
fun MarqueeText(
    text: String,
    modifier: Modifier = Modifier,
    gradientEdgeColor: Color = Color.White,
    scrollDirection: ScrollDirection = ScrollDirection.Forward,
    scrollStartDelay: Int = 2000,
    textEndsSpacing: Int = SPACING_LARGE,
    edgeGradientWidth: Dp = 12.dp,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    val createText = @Composable { localModifier: Modifier ->
        Text(
            text,
            textAlign = textAlign,
            modifier = localModifier,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = 1,
            onTextLayout = onTextLayout,
            style = style,
        )
    }
    var mainTextOffset by remember { mutableStateOf(0) }
    val textLayoutInfoState = remember { mutableStateOf<TextLayoutInfo?>(null) }

    val scrollAnimationUpdateDelay = 1000L

    LaunchedEffect(textLayoutInfoState.value) {
        val textLayoutInfo = textLayoutInfoState.value ?: return@LaunchedEffect

        val duration = 7500 * textLayoutInfo.textWidth / textLayoutInfo.containerWidth

        val scrollInitialValue = when (scrollDirection) {
            ScrollDirection.Forward -> 0
            ScrollDirection.Backward -> -textLayoutInfo.textWidth
        }
        val scrollTargetValue = when (scrollDirection) {
            ScrollDirection.Forward -> -textLayoutInfo.textWidth
            ScrollDirection.Backward -> 0
        }
        val scrollingAnimation = TargetBasedAnimation(
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = duration,
                    delayMillis = scrollStartDelay,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Restart
            ),
            typeConverter = Int.VectorConverter,
            initialValue = scrollInitialValue,
            targetValue = scrollTargetValue
        )
        do {
            val scrollStartTime = withFrameNanos { it }
            do {
                val scrollPlayTime = withFrameNanos { it } - scrollStartTime
                mainTextOffset = scrollingAnimation.getValueFromNanos(scrollPlayTime)
            } while (!scrollingAnimation.isFinishedFromNanos(scrollPlayTime))
            delay(scrollAnimationUpdateDelay)
        } while (true)
    }

    SubcomposeLayout(modifier.clipToBounds()) { constraints ->
        val infiniteWidthConstraints = constraints.copy(maxWidth = Int.MAX_VALUE)
        var nextText: Placeable? = null

        val endsSpacing = when (textEndsSpacing) {
            0 -> textEndsSpacing
            else -> constraints.maxWidth / textEndsSpacing
        }

        val mainText = measureSubcompose(
            MarqueeComponents.MainText,
            infiniteWidthConstraints
        ) {
            createText(Modifier)
        }
        val mainTextWidth = mainText.width
        val maxContainerWidth = constraints.maxWidth
        val nextTextOffset = mainTextWidth + mainTextOffset + endsSpacing

        textLayoutInfoState.value = TextLayoutInfo(
            textWidth = mainTextWidth + endsSpacing,
            containerWidth = maxContainerWidth
        )
        if (maxContainerWidth >= nextTextOffset) {
            nextText = measureSubcompose(MarqueeComponents.NextText, infiniteWidthConstraints) {
                createText(Modifier)
            }
        }
        val gradient = measureSubcompose(
            MarqueeComponents.EdgesGradient,
            constraints.copy(maxHeight = mainText.height)
        ) {
            Row {
                GradientEdge(
                    width = edgeGradientWidth,
                    startColor = Color.Transparent,
                    endColor = gradientEdgeColor
                )
                Spacer(Modifier.weight(1f))
                GradientEdge(
                    width = edgeGradientWidth,
                    startColor = gradientEdgeColor,
                    endColor = Color.Transparent
                )
            }

        }

        layout(
            width = constraints.maxWidth,
            height = mainText.height
        ) {
            mainText.place(mainTextOffset, 0)
            nextText?.place(nextTextOffset, 0)
            gradient.place(0, 0)
        }
    }
}

@Composable
private fun GradientEdge(
    width: Dp,
    startColor: Color,
    endColor: Color
) {
    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .background(
                brush = Brush.horizontalGradient(
                    0f to startColor, 1f to endColor,
                )
            )
    )
}

private fun SubcomposeMeasureScope.measureSubcompose(
    subcomposeSlotId: Any?,
    measureConstraints: Constraints,
    content: @Composable () -> Unit
): Placeable {
    return  subcompose(subcomposeSlotId, content).first().measure(measureConstraints)
}
