package com.skyyo.samples.features.textGradient

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skyyo.samples.features.textGradient.composables.RunningGradientText
import com.skyyo.samples.features.textGradient.composables.ShimmerGradientText

// Taken from https://medium.com/androiddevelopers/animating-brush-text-coloring-in-compose-%EF%B8%8F-26ae99d9b402
@Composable
fun TextGradientScreen() {
    val sampleText = "Uniqueness is not inner in order, the mind, or over there, but everywhere. " +
        "Confucius says: the one guru is existing, the further lotus is empowering. " +
        "You have to convert, and witness milk by your luring."
    val fontSize = remember { 18.sp }
    val fontWeight = remember { FontWeight.Bold }
    val animationDurationMillisSmall = remember { 500 }
    val gradientDarkNeonColors = remember {
        listOf(
            Color(0xFF000000),
            Color(0xFF52057B),
            Color(0xFF892CDC),
            Color(0xFFBC6FF1)
        )
    }
    val gradientLightNeonColors = remember {
        listOf(
            Color(0xFFF35588),
            Color(0xFF05DFD7),
            Color(0xFFA3F7BF),
            Color(0xFFFFF591)
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        RunningGradientText(
            text = sampleText,
            fontSize = fontSize,
            colors = gradientDarkNeonColors,
            fontWeight = fontWeight
        )
        RunningGradientText(
            text = sampleText,
            fontSize = fontSize,
            fontWeight = fontWeight,
            animationDurationMillis = animationDurationMillisSmall,
            colors = gradientLightNeonColors
        )
        ShimmerGradientText(
            text = sampleText,
            fontSize = fontSize,
            colors = gradientDarkNeonColors,
            fontWeight = fontWeight
        )
        ShimmerGradientText(
            text = sampleText,
            fontSize = fontSize,
            fontWeight = fontWeight,
            animationDurationMillis = animationDurationMillisSmall,
            colors = gradientLightNeonColors
        )
    }
}
