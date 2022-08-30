package com.skyyo.samples.features.parallaxEffect

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import kotlin.math.min

const val IMAGE_HEIGHT = 256

@OptIn(ExperimentalCoilApi::class)
@Suppress("MagicNumber")
@Composable
fun ParallaxEffectScreen() {
    val scrollState = rememberScrollState()
    val imageAlpha = min(1f, 1 - scrollState.value / 600f)
    val imageOffsetY = scrollState.value * 0.1f
    val imageHeight = (IMAGE_HEIGHT.dp - imageOffsetY.dp).coerceAtLeast(0.dp)

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = imageHeight)
                .verticalScroll(scrollState)
        ) {
            Text(
                fontSize = 24.sp,
                text = "awdawdawdawd awdawdawdawd awdawdawdawdawdawdawdawdawdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd \"awdawdawdawd awdawdawdawd awdawdawdawdawdawdawdawdawdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd \" \"awdawdawdawd awdawdawdawd awdawdawdawdawdawdawdawdawdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd \"\"awdawdawdawd awdawdawdawd awdawdawdawdawdawdawdawdawdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd \" \"awdawdawdawd awdawdawdawd awdawdawdawdawdawdawdawdawdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd \" \"awdawdawdawd awdawdawdawd awdawdawdawdawdawdawdawdawdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd \" \"awdawdawdawd awdawdawdawd awdawdawdawdawdawdawdawdawdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd \" v \"awdawdawdawd awdawdawdawd awdawdawdawdawdawdawdawdawdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd \" v \"awdawdawdawd awdawdawdawd awdawdawdawdawdawdawdawdawdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd \" \"awdawdawdawd awdawdawdawd awdawdawdawdawdawdawdawdawdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd \" \"awdawdawdawd awdawdawdawd awdawdawdawdawdawdawdawdawdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd \" v v \"awdawdawdawd awdawdawdawd awdawdawdawdawdawdawdawdawdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd \" \"awdawdawdawd awdawdawdawd awdawdawdawdawdawdawdawdawdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd \" \"awdawdawdawd awdawdawdawd awdawdawdawdawdawdawdawdawdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd \" \"awdawdawdawd awdawdawdawd awdawdawdawdawdawdawdawdawdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd \" \"awdawdawdawd awdawdawdawd awdawdawdawdawdawdawdawdawdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd \" \"awdawdawdawd awdawdawdawd awdawdawdawdawdawdawdawdawdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd awdawdawdawd \" v v"
            )
        }
        Image(
            painter = rememberAsyncImagePainter("https://placekitten.com/g/200/300"),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .graphicsLayer {
                    com.skyyo.samples.extensions.log("newY $imageOffsetY")
                    alpha = imageAlpha
                    translationY = -imageOffsetY
                }
        )
    }
}
