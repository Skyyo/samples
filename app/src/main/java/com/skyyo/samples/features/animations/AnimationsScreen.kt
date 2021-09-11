package com.skyyo.samples.features.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimationsScreen() {
    val isPlaying = remember { mutableStateOf(false) }
    val direction = remember { mutableStateOf(TranslationDirection.TOP_TO_BOTTOM) }

    Column(Modifier.fillMaxHeight()) {
        //TODO insets
        Spacer(modifier = Modifier.height(50.dp))
        Button(onClick = { isPlaying.value = !isPlaying.value}) {
            Text(text = if (isPlaying.value) "Stop" else "Start")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {direction.value = direction.value.toggle()}) {
            Text(text = "change direction")
        }
        Spacer(modifier = Modifier.height(10.dp))
        BoxWithConstraints {
            if (isPlaying.value) {
                TranslationableLabel(direction = direction.value, height = maxHeight)
            } else {
                Label()
            }
        }
    }
}

private fun TranslationDirection.toggle() = when(this) {
    TranslationDirection.TOP_TO_BOTTOM -> TranslationDirection.BOTTOM_TO_TOP
    TranslationDirection.BOTTOM_TO_TOP -> TranslationDirection.TOP_TO_BOTTOM
}

@Composable
fun TranslationableLabel(direction: TranslationDirection, height: Dp) {
    val movingTransition = rememberInfiniteTransition()
    val draggingAnimationState = movingTransition.animateFloat(
        initialValue = if (direction == TranslationDirection.TOP_TO_BOTTOM) 0f else 1f ,
        targetValue = if (direction == TranslationDirection.TOP_TO_BOTTOM) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(
            durationMillis = 4000,
            easing = LinearEasing
        ))
    )
    Label(modifier = Modifier.offset(y = height * draggingAnimationState.value))
}

@Composable
fun Label(modifier: Modifier = Modifier) {
    Text(text = "Moveable Label", modifier = modifier)
}