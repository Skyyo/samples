@file:Suppress("MagicNumber")

package com.skyyo.samples.features.customView

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun CustomViewScreen() {
    val sliderValue by remember { mutableStateOf(0.5f) }

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Triangle(sliderValue)
        CustomProgressCircle(sliderValue)
        CustomProgressCircle2(sliderValue)
    }
}

@Composable
fun CustomProgressCircle2(sliderValue: Float) {
    Canvas(
        modifier = Modifier
            .width(250.dp)
            .height(250.dp)
            .padding(16.dp)
    ) {
        drawArc(
            brush = SolidColor(Color.LightGray),
            startAngle = 120f,
            sweepAngle = 300f,
            useCenter = false,
            style = Stroke(width = 35f, cap = StrokeCap.Round)
        )

        val convertedValue = sliderValue * 300

        drawArc(
            brush = SolidColor(Color.Cyan),
            startAngle = 120f,
            sweepAngle = convertedValue,
            useCenter = false,
            style = Stroke(width = 35f, cap = StrokeCap.Round)
        )

        drawIntoCanvas {
            val paint = Paint().asFrameworkPaint()
            paint.apply {
                isAntiAlias = true
                textSize = 55f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            // TODO this should be replaced by one of the compose API's in future
            it.nativeCanvas.drawText(
                "${(sliderValue * 100).roundToInt()}%",
                size.width / 2,
                size.height / 2,
                paint
            )
        }
    }
}

@Composable
fun CustomProgressCircle(sliderValue: Float) {
    Canvas(
        modifier = Modifier
            .size(250.dp)
            .padding(16.dp)
    ) {
        drawCircle(
            SolidColor(Color.LightGray),
            size.width / 2,
            style = Stroke(width = 35f)
        )
        val convertedValue = sliderValue * 360
        drawArc(
            brush = SolidColor(Color.Black),
            startAngle = -90f,
            sweepAngle = convertedValue,
            useCenter = false,
            style = Stroke(width = 35f)
        )
    }
}

@Composable
fun Triangle(sliderValue: Float) {
    Canvas(
        modifier = Modifier
            .width(300.dp)
            .height(150.dp)
            .padding(16.dp)
    ) {
        val path = Path()
        path.moveTo(size.width, 0f)
        path.lineTo(size.width, size.height)
        path.lineTo(0f, size.height)

        clipPath(clipOp = ClipOp.Intersect, path = path) {
            drawPath(
                path = path,
                brush = SolidColor(Color.LightGray)
            )

            drawRect(
                SolidColor(Color.Green),
                size = Size(
                    sliderValue * size.width,
                    size.height
                )
            )
        }
    }
}
