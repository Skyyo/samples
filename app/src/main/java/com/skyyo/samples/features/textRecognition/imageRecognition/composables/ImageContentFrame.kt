package com.skyyo.samples.features.textRecognition.imageRecognition.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skyyo.samples.utils.OnClick

@Composable
fun ImageContentFrame(
    modifier: Modifier = Modifier,
    borderStrokeWidth: Dp = 2.dp,
    borderStrokeLength: Dp = 6.dp,
    borderStrokeStep: Dp = borderStrokeLength,
    onClick: OnClick,
    imageBitmap: ImageBitmap?
) {
    val density = LocalDensity.current
    val borderStrokeWidthPx = remember { with(density) { borderStrokeWidth.toPx() } }
    val borderStrokeWidthHalfPx = remember { borderStrokeWidthPx / 2 }
    val borderStrokeLengthPx = remember { with(density) { borderStrokeLength.toPx() } }
    val borderStrokeStepPx = remember { with(density) { borderStrokeStep.toPx() } }
    val borderTopLeftOffset = remember { Offset(borderStrokeWidthHalfPx, borderStrokeWidthHalfPx) }
    val pathEffect = remember {
        PathEffect.dashPathEffect(floatArrayOf(borderStrokeLengthPx, borderStrokeStepPx))
    }
    val dashedStrokeStyle = remember {
        Stroke(width = borderStrokeWidthPx, pathEffect = pathEffect)
    }
    Box(
        modifier = modifier
            .wrapContentSize()
            .heightIn(max = 400.dp)
            .drawBehind {
                val borderSize = Size(
                    width = size.width - borderStrokeWidthPx,
                    height = size.height - borderStrokeWidthPx
                )
                drawRect(
                    topLeft = borderTopLeftOffset,
                    size = borderSize,
                    color = Color.Blue,
                    style = dashedStrokeStyle
                )
            }
            .padding(borderStrokeWidth)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (imageBitmap == null) {
            Box(modifier = Modifier.height(320.dp), contentAlignment = Alignment.Center) {
                Text(
                    modifier = Modifier.padding(34.dp),
                    text = "Choose image source",
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.W500,
                        textAlign = TextAlign.Center
                    ),
                    color = Color.Black
                )
            }
        } else {
            Image(
                bitmap = imageBitmap,
                contentDescription = null,
                contentScale = ContentScale.Inside
            )
        }
    }
}
