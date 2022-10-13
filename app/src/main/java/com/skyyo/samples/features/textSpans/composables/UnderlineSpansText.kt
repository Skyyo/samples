package com.skyyo.samples.features.textSpans.composables

import android.graphics.Region
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import com.skyyo.samples.features.textSpans.buildSquigglesFor
import com.skyyo.samples.features.textSpans.getBoundingBoxes

@Composable
fun AnimatedSquiggleWavelengthUnderlineText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    isCorrect: Boolean = true,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified
) {
    val density = LocalDensity.current
    val onDraw: MutableState<DrawScope.() -> Unit> = remember { mutableStateOf({}) }
    val path = remember { Path() }
    val region = remember { Region() }
    val animationProgress by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val correctWaveLength = with(density) { 10.sp.toPx() }
    val incorrectWaveLength = with(density) { 120.sp.toPx() }
    val waveLengthPx by animateFloatAsState(targetValue = if (isCorrect) correctWaveLength else incorrectWaveLength)
    val pathColor by animateColorAsState(targetValue = if (isCorrect) Color.Green else Color.Red)
    val pathEffect =
        remember { PathEffect.cornerPathEffect(radius = waveLengthPx) } // For slightly smoother waves. }
    val pathStyle = remember {
        Stroke(
            width = with(density) { 2.sp.toPx() },
            join = StrokeJoin.Round,
            cap = StrokeCap.Round,
            pathEffect = pathEffect
        )
    }
    val annotation =
        remember(text) { text.getStringAnnotations("squiggles", 0, text.length).first() }
    Text(
        modifier = modifier.drawBehind { onDraw.value(this) },
        text = text,
        fontSize = fontSize,
        lineHeight = lineHeight,
        onTextLayout = { layoutResult ->
            val textBounds = layoutResult.getBoundingBoxes(region, annotation.start, annotation.end)
            onDraw.value = {
                for (bound in textBounds) {
                    path.apply {
                        reset()
                        buildSquigglesFor(
                            box = bound,
                            density = density,
                            waveOffset = animationProgress,
                            waveLength = waveLengthPx.toSp()
                        )
                    }
                    drawPath(
                        color = pathColor,
                        path = path,
                        style = pathStyle
                    )
                }
            }
        }
    )
}

@Composable
fun AnimatedSquiggleUnderlineText(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    underlineWidth: TextUnit = 2.sp,
    waveLength: TextUnit = 10.sp,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified
) {
    val density = LocalDensity.current
    val onDraw: MutableState<DrawScope.() -> Unit> = remember { mutableStateOf({}) }
    val path = remember { Path() }
    val region = remember { Region() }
    val animationProgress by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val pathEffect =
        remember { PathEffect.cornerPathEffect(radius = with(density) { waveLength.toPx() }) } // For slightly smoother waves.
    val pathStyle = remember {
        Stroke(
            width = with(density) { underlineWidth.toPx() },
            join = StrokeJoin.Round,
            cap = StrokeCap.Round,
            pathEffect = pathEffect
        )
    }
    val annotation =
        remember(text) { text.getStringAnnotations("squiggles", 0, text.length).first() }
    Text(
        modifier = modifier.drawBehind { onDraw.value(this) },
        text = text,
        fontSize = fontSize,
        lineHeight = lineHeight,
        onTextLayout = { layoutResult ->
            val textBounds = layoutResult.getBoundingBoxes(region, annotation.start, annotation.end)
            onDraw.value = {
                for (bound in textBounds) {
                    path.apply {
                        reset()
                        buildSquigglesFor(
                            box = bound,
                            density = density,
                            waveOffset = animationProgress,
                            waveLength = waveLength
                        )
                    }
                    drawPath(
                        color = Color.Green,
                        path = path,
                        style = pathStyle
                    )
                }
            }
        }
    )
}

@Composable
fun SquiggleUnderlineText(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    underlineWidth: TextUnit = 2.sp,
    waveLength: TextUnit = 10.sp,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified
) {
    val density = LocalDensity.current
    val onDraw: MutableState<DrawScope.() -> Unit> = remember { mutableStateOf({}) }
    val path = remember { Path() }
    val region = remember { Region() }
    val pathEffect =
        remember { PathEffect.cornerPathEffect(radius = with(density) { waveLength.toPx() }) } // For slightly smoother waves.
    val pathStyle = remember {
        Stroke(
            width = with(density) { underlineWidth.toPx() },
            join = StrokeJoin.Round,
            cap = StrokeCap.Round,
            pathEffect = pathEffect
        )
    }
    val annotation =
        remember(text) { text.getStringAnnotations("squiggles", 0, text.length).first() }
    Text(
        modifier = modifier.drawBehind { onDraw.value(this) },
        text = text,
        fontSize = fontSize,
        lineHeight = lineHeight,
        onTextLayout = { layoutResult ->
            val textBounds = layoutResult.getBoundingBoxes(region, annotation.start, annotation.end)
            onDraw.value = {
                for (bound in textBounds) {
                    path.apply {
                        reset()
                        buildSquigglesFor(bound, density)
                    }
                    drawPath(
                        color = Color.Green,
                        path = path,
                        style = pathStyle
                    )
                }
            }
        }
    )
}

data class SimplePaddingValues(val horizontal: Dp, val vertical: Dp)

@Composable
fun HighlightedText(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    cornerRadius: CornerRadius,
    highlightColor: Color,
    highlightBorderColor: Color? = null,
    highlightBorderWidth: Dp = 2.dp,
    padding: SimplePaddingValues = remember {
        SimplePaddingValues(
            horizontal = 0.dp,
            vertical = 0.dp
        )
    },
) {
    val onDraw: MutableState<DrawScope.() -> Unit> = remember { mutableStateOf({}) }
    val path = remember { Path() }
    val region = remember { Region() }
    val highlightBorderWidthPx = with(LocalDensity.current) { highlightBorderWidth.toPx() }
    val highlightBorderStroke = remember { Stroke(width = highlightBorderWidthPx) }
    val annotation =
        remember(text) { text.getStringAnnotations("squiggles", 0, text.length).first() }
    Text(
        modifier = modifier.drawBehind { onDraw.value(this) },
        text = text,
        fontSize = fontSize,
        lineHeight = lineHeight,
        onTextLayout = { layoutResult ->
            val textBounds = layoutResult.getBoundingBoxes(region, annotation.start, annotation.end)
            onDraw.value = {
                textBounds.fastForEachIndexed { index, bound ->
                    path.apply {
                        reset()
                        addRoundRect(
                            RoundRect(
                                rect = bound.copy(
                                    left = bound.left - padding.horizontal.toPx(),
                                    right = bound.right + padding.horizontal.toPx(),
                                    top = bound.top - padding.vertical.toPx(),
                                    bottom = bound.bottom + padding.vertical.toPx()
                                ),
                                topLeft = if (index == 0) cornerRadius else CornerRadius.Zero,
                                bottomLeft = if (index == 0) cornerRadius else CornerRadius.Zero,
                                topRight = if (index == textBounds.lastIndex) cornerRadius else CornerRadius.Zero,
                                bottomRight = if (index == textBounds.lastIndex) cornerRadius else CornerRadius.Zero
                            )
                        )
                    }
                    drawPath(
                        path = path,
                        color = highlightColor,
                        style = Fill
                    )
                    if (highlightBorderColor != null) {
                        drawPath(
                            path = path,
                            color = highlightBorderColor,
                            style = highlightBorderStroke
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun UnderlinedText(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    underlineWidth: TextUnit = 2.sp,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified
) {
    val region = remember { Region() }
    val underlineWidthPxHalf = with(LocalDensity.current) { underlineWidth.toPx() / 2 }
    val onDraw: MutableState<DrawScope.() -> Unit> = remember { mutableStateOf({}) }
    val annotation =
        remember(text) { text.getStringAnnotations("squiggles", 0, text.length).first() }
    Text(
        modifier = modifier.drawBehind { onDraw.value(this) },
        text = text,
        fontSize = fontSize,
        lineHeight = lineHeight,
        onTextLayout = { layoutResult ->
            val textBounds = layoutResult.getBoundingBoxes(region, annotation.start, annotation.end)
            onDraw.value = {
                for (bound in textBounds) {
                    drawLine(
                        color = Color.Green,
                        start = Offset(bound.left, bound.bottom + underlineWidthPxHalf),
                        end = Offset(bound.right, bound.bottom + underlineWidthPxHalf),
                        strokeWidth = underlineWidth.toPx()
                    )
                }
            }
        }
    )
}
