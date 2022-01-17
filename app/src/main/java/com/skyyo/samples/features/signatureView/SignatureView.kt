package com.skyyo.samples.features.signatureView

import android.graphics.Bitmap
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
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
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignatureView(
    modifier: Modifier = Modifier,
    stroke: Stroke = Stroke(10f),
    paintColor: Color = Color.Black,
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
    val savedList = rememberSaveable { mutableListOf<Pair<Boolean, Pair<Float, Float>>>() }
    val triggerList = remember {
        if (savedList.isEmpty()) {
            mutableStateListOf()
        } else {
            savedList.toMutableStateList()
        }
    }

    fun getBitmap(): Bitmap {
        val bounds = viewBounds.value
        val bitmap = Bitmap.createBitmap(
            bounds.width.roundToInt(), bounds.height.roundToInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvasForSnapshot = Canvas(bitmap.asImageBitmap())
        canvasForSnapshot.drawPath(savedList.toPath(), paint)

        return bitmap
    }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                SignatureViewEvent.Reset -> {
                    triggerList.clear()
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
            .pointerInteropFilter {
                val x = it.x
                val y = it.y
                val value: Pair<Boolean, Pair<Float, Float>>
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        value = Pair(true, Pair(x, y))
                        triggerList.add(value)
                    }
                    MotionEvent.ACTION_MOVE -> {
                        value = Pair(false, Pair(x, y))
                        triggerList.add(value)
                    }
                }
                true
            },
    ) {
        triggerList.run {
            if (triggerList.isNotEmpty()) savedList.clear()
            savedList.addAll(triggerList)

            drawPath(
                path = savedList.toPath(),
                color = paintColor,
                alpha = 1f,
                style = Stroke(4f)
            )
        }
    }
}

fun List<Pair<Boolean, Pair<Float, Float>>>.toPath(): Path {
    val path = Path()
    forEach {
        if (it.first) {
            path.moveTo(it.second.first, it.second.second)
        } else {
            path.lineTo(it.second.first, it.second.second)
        }
    }
    return path
}
