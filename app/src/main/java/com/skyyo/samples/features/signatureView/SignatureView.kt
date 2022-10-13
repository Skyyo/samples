package com.skyyo.samples.features.signatureView

import android.graphics.Bitmap
import android.os.Parcelable
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

@Parcelize
data class MotionEventValue(val eventType: Int, val x: Float, val y: Float) : Parcelable

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignatureView(
    modifier: Modifier = Modifier,
    stroke: Stroke = Stroke(10f),
    paintColor: Color = Color.Black,
    canvasColor: Color = Color.Transparent,
    events: Flow<SignatureViewEvent>,
    onBitmapSaved: (bitmap: Bitmap) -> Unit
) {
    val viewBounds = remember { mutableStateOf(Rect.Zero) }
    val paint = remember {
        Paint().apply {
            style = PaintingStyle.Stroke
            color = paintColor
            strokeWidth = stroke.width
        }
    }
    val savedList = rememberSaveable { mutableListOf<MotionEventValue>() }
    val motionEventValue = remember { mutableStateOf<MotionEventValue?>(null) }

    fun getBitmap(): Bitmap {
        val bounds = viewBounds.value
        val bitmap = Bitmap.createBitmap(
            bounds.width.roundToInt(), bounds.height.roundToInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvasForSnapshot = Canvas(bitmap.asImageBitmap()).apply {
            nativeCanvas.drawColor(canvasColor.toArgb())
        }
        canvasForSnapshot.drawPath(savedList.toPath(), paint)

        return bitmap
    }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                SignatureViewEvent.Reset -> {
                    motionEventValue.value = null
                    savedList.clear()
                }
                SignatureViewEvent.Save -> {
                    val bitmap = getBitmap()
                    onBitmapSaved(bitmap)
                }
            }
        }
    }

    Canvas(
        modifier = modifier
            .onGloballyPositioned { viewBounds.value = it.boundsInRoot() }
            .clipToBounds()
            .background(canvasColor)
            .pointerInteropFilter {
                val x = it.x
                val y = it.y
                val value: MotionEventValue
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        value = MotionEventValue(it.action, x,y)
                        motionEventValue.value = value
                    }
                    MotionEvent.ACTION_MOVE -> {
                        value = MotionEventValue(it.action, x,y)
                        motionEventValue.value = value
                    }
                }
                true
            },
    ) {
        motionEventValue.value.run {
            if (this != null) savedList.add(this)
            drawPath(
                path = savedList.toPath(),
                color = paintColor,
                style = stroke
            )
        }
    }
}

fun List<MotionEventValue>.toPath(): Path {
    val path = Path()
    forEach { value ->
        if (value.eventType == MotionEvent.ACTION_DOWN) {
            path.moveTo(value.x, value.y)
        } else {
            path.lineTo(value.x, value.y)
        }
    }
    return path
}
