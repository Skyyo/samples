package com.skyyo.samples.features.signatureView

import android.graphics.Bitmap
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignatureView(
    modifier: Modifier = Modifier,
    stroke: Stroke,
    paintColor: Color = Color.Black,
    canvasColor: Color = Color.Transparent,
    events: Flow<SignatureViewEvent>,
    onSaveBitmap: (bitmap: Bitmap) -> Unit,
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

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                SignatureViewEvent.Reset -> {
                    savedList.clear()
                    motionEventValue.value = MotionEventValue(MotionEvent.ACTION_DOWN, 0.0f, 0.0f)
                }
                SignatureViewEvent.Save -> {
                    val bitmap = SignatureViewUtils.getBitmap(
                        viewBounds.value,
                        canvasColor.toArgb(),
                        savedList.toPath(),
                        paint
                    )
                    onSaveBitmap(bitmap)
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
                        value = MotionEventValue(it.action, x, y)
                        motionEventValue.value = value
                    }
                    MotionEvent.ACTION_MOVE -> {
                        value = MotionEventValue(it.action, x, y)
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
