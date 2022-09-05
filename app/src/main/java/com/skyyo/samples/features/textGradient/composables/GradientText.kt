package com.skyyo.samples.features.textGradient.composables

import androidx.compose.animation.core.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalTextApi::class)
@Composable
fun RunningGradientText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    colors: List<Color>,
    animationDurationMillis: Int = 1000
) {
    val density = LocalDensity.current
    val currentFontSizePx = remember { with(density) { fontSize.toPx() } }
    val currentFontSizeDoublePx = remember { currentFontSizePx * 2 }

    val infiniteTransition = rememberInfiniteTransition()
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = currentFontSizeDoublePx,
        animationSpec = infiniteRepeatable(tween(animationDurationMillis, easing = LinearEasing))
    )
    val brush = remember(offset) {
        Brush.linearGradient(
            colors = colors,
            start = Offset(offset, offset),
            end = Offset(offset + currentFontSizePx, offset + currentFontSizePx),
            tileMode = TileMode.Mirror
        )
    }
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(
            fontSize = fontSize,
            fontWeight = fontWeight,
            brush = brush
        )
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun ShimmerGradientText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    colors: List<Color>,
    animationDurationMillis: Int = 2000
) {
    val infiniteTransition = rememberInfiniteTransition()
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDurationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val brush = remember(offset) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val widthOffset = size.width * offset
                val heightOffset = size.height * offset
                return LinearGradientShader(
                    colors = colors,
                    from = Offset(widthOffset, heightOffset),
                    to = Offset(widthOffset + size.width, heightOffset + size.height),
                    tileMode = TileMode.Mirror
                )
            }
        }
    }
    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(
            fontSize = fontSize,
            fontWeight = fontWeight,
            brush = brush
        )
    )
}
