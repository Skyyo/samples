package com.skyyo.samples.features.gradientScroll

import android.animation.ArgbEvaluator
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.skyyo.samples.theme.Shapes


data class ChatMessage(val message: String)

private val topColorArgb = Color(0xFF106CCF).toArgb()
private val bottomColorArgb = Color(0xFFE7D21A).toArgb()

@Composable
fun GradientScrollScreen() {
    val messages = remember {
        (0..100).map { ChatMessage(message = "Message$it") }
    }
    var columnHeight by remember { mutableStateOf(0f) }
    val argbEvaluator = remember { ArgbEvaluator() }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                columnHeight = coordinates.size.height.toFloat()
            },
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = true,
            applyBottom = true,
            additionalStart = 16.dp,
            additionalEnd = 16.dp,
            additionalBottom = 8.dp
        )
    ) {
        itemsIndexed(messages, key = { index, _ -> index }) { _, chatMessage ->
            GradientMessage(
                message = chatMessage.message,
                columnHeight = columnHeight,
                evaluator = argbEvaluator
            )
        }
    }
}

@Composable
fun GradientMessage(
    message: String,
    columnHeight: Float,
    evaluator: ArgbEvaluator
) {
    var backgroundColor by remember { mutableStateOf(Color.Transparent) }

    Text(
        text = message,
        color = Color.White,
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp)
            .onGloballyPositioned { coordinates: LayoutCoordinates ->
                val topOffset = coordinates.boundsInParent().top
                val cleanTopOffset = when {
                    topOffset < 0 -> 0f
                    topOffset > columnHeight -> columnHeight
                    else -> topOffset
                }
                backgroundColor = calculateGradient(
                    progress = cleanTopOffset / columnHeight,
                    evaluator = evaluator
                )
            }
            .background(
                color = backgroundColor,
                shape = Shapes.large
            )
            .padding(vertical = 8.dp, horizontal = 16.dp),
    )
}


fun calculateGradient(progress: Float, evaluator: ArgbEvaluator): Color {
    return Color(evaluator.evaluate(progress, topColorArgb, bottomColorArgb) as Int)
}