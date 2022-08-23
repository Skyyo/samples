package com.skyyo.samples.features.textSpans.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
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
    val pathStyle = Stroke(
        width = with(density) { 2.sp.toPx() },
        join = StrokeJoin.Round,
        cap = StrokeCap.Round,
        pathEffect = PathEffect.cornerPathEffect(radius = waveLengthPx), // For slightly smoother waves.
    )
    Text(
        modifier = modifier.drawBehind { onDraw.value(this) },
        text = text,
        fontSize = fontSize,
        lineHeight = lineHeight,
        onTextLayout = { layoutResult ->
            val annotation = text.getStringAnnotations("squiggles", 0, text.length).first()
            val textBounds = layoutResult.getBoundingBoxes(annotation.start, annotation.end)
            onDraw.value = {
                for (bound in textBounds) {
                    val path = Path().apply {
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
    val animationProgress by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val pathStyle = Stroke(
        width = with(density) { underlineWidth.toPx() },
        join = StrokeJoin.Round,
        cap = StrokeCap.Round,
        pathEffect = PathEffect.cornerPathEffect(radius = with(density) { waveLength.toPx() }), // For slightly smoother waves.
    )
    Text(
        modifier = modifier.drawBehind { onDraw.value(this) },
        text = text,
        fontSize = fontSize,
        lineHeight = lineHeight,
        onTextLayout = { layoutResult ->
            val annotation = text.getStringAnnotations("squiggles", 0, text.length).first()
            val textBounds = layoutResult.getBoundingBoxes(annotation.start, annotation.end)
            onDraw.value = {
                for (bound in textBounds) {
                    val path =
                        Path().apply {
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
    val pathStyle = Stroke(
        width = with(density) { underlineWidth.toPx() },
        join = StrokeJoin.Round,
        cap = StrokeCap.Round,
        pathEffect = PathEffect.cornerPathEffect(radius = with(density) { waveLength.toPx() }), // For slightly smoother waves.
    )
    Text(
        modifier = modifier.drawBehind { onDraw.value(this) },
        text = text,
        fontSize = fontSize,
        lineHeight = lineHeight,
        onTextLayout = { layoutResult ->
            val annotation = text.getStringAnnotations("squiggles", 0, text.length).first()
            val textBounds = layoutResult.getBoundingBoxes(annotation.start, annotation.end)
            onDraw.value = {
                for (bound in textBounds) {
                    val path = Path().also { it.buildSquigglesFor(bound, density) }
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
