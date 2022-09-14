package com.skyyo.samples.features.transparentBlur

import android.graphics.*
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.*
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.packFloats
import androidx.compose.ui.util.unpackFloat1
import androidx.compose.ui.util.unpackFloat2
import androidx.core.graphics.scale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.skyyo.samples.R
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow

private class ImageResource(@DrawableRes val resourceId: Int, val filter: ColorFilter? = null)

@Composable
fun GlassBlurScreen() {
    val imageResources = remember { provideImageResources() }
    val rowArrangement = remember { Arrangement.spacedBy(20.dp) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        Column(
            modifier = Modifier
                .background(Color.Cyan)
                .align(Alignment.Center)
                .padding(vertical = 20.dp, horizontal = 6.dp),
            verticalArrangement = remember { Arrangement.spacedBy(20.dp) }
        ) {
            repeat(imageResources.size) { columnIndex ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = rowArrangement
                ) {
                    repeat(imageResources[columnIndex].size) { rowIndex ->
                        val imageResource = imageResources[columnIndex][rowIndex]
                        Image(
                            modifier = Modifier.heightIn(max = 35.dp),
                            painter = painterResource(id = imageResource.resourceId),
                            contentDescription = "",
                            colorFilter = imageResource.filter
                        )
                    }
                }
            }
        }
        GlassBlurTarget()
    }
}

@Composable
fun BoxWithConstraintsScope.GlassBlurTarget(targetSize: Dp = 80.dp) {
    val draggableArea = remember {
        val minXOffset = 0.dp
        val minYOffset = 0.dp
        val maxXOffset = maxWidth
        val maxYOffset = maxHeight
        DpRect(minXOffset, minYOffset, maxXOffset, maxYOffset)
    }
    val size = remember(targetSize) { DpSize(targetSize, targetSize) }

    Spacer(
        modifier = Modifier
            .draggable(size, draggableArea)
            .size(targetSize)
            .border(1.dp, Color.Red)
            .glassBlur()
    )
}

private fun Modifier.draggable(size: DpSize, draggableArea: DpRect) = composed {
    var offset by rememberSaveable(inputs = arrayOf(draggableArea), stateSaver = OffsetSaver()) {
        val centerX = (draggableArea.left + draggableArea.right) / 2 - size.width / 2
        val centerY = (draggableArea.top + draggableArea.bottom) / 2 - size.height / 2
        mutableStateOf(DpOffset(centerX, centerY))
    }

    pointerInput(size, draggableArea) {
        forEachGesture {
            awaitPointerEventScope {
                val down = awaitFirstDown(requireUnconsumed = false)
                drag(down.id) {
                    val xOffset = (it.position.x.toDp() - size.width / 2).coerceIn(draggableArea.left, draggableArea.right - size.width)
                    val yOffset = (it.position.y.toDp() - size.height / 2).coerceIn(draggableArea.top, draggableArea.bottom - size.height)
                    offset = DpOffset(xOffset, yOffset)
                    if (it.positionChange() != Offset.Zero) it.consume()
                }
            }
        }
    }.padding(
        start = offset.x,
        top = offset.y
    )
}

private class OffsetSaver : Saver<DpOffset, Long> {
    override fun restore(value: Long): DpOffset {
        return DpOffset(unpackFloat1(value).dp, unpackFloat2(value).dp)
    }

    override fun SaverScope.save(value: DpOffset): Long {
        return packFloats(value.x.value, value.y.value)
    }
}

private fun Modifier.glassBlur(radius: Int = 6) = composed {
    val view = LocalView.current
    val bitmap = remember { Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888) }
    val canvas = remember { Canvas(bitmap) }
    val currentBoundsChannel = remember { Channel<Rect>(Channel.UNLIMITED) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentBoundsFlow = remember(currentBoundsChannel, lifecycle) {
        currentBoundsChannel.receiveAsFlow().flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
    }
    val isBlurredBitmapObsolete = remember { Ref<Boolean>().also { it.value = true } }
    var blurredBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(currentBoundsFlow) {
        currentBoundsFlow.distinctUntilChanged().collect {
            isBlurredBitmapObsolete.value = true
            view.draw(canvas)
            blurredBitmap = withContext(Dispatchers.IO) {
                bitmap.cut(it).blur(radius).asImageBitmap()
            }
            isBlurredBitmapObsolete.value = false
        }
    }

    onGloballyPositioned {
        currentBoundsChannel.trySend(it.boundsInWindow().toAndroidRect())
    }.drawWithContent {
        drawContent()
        val blurredImageBitmap = blurredBitmap
        if (blurredImageBitmap != null && !isBlurredBitmapObsolete.value!!) {
            drawImage(blurredImageBitmap)
        }
    }
}

private fun Bitmap.cut(rect: Rect): Bitmap {
    return Bitmap.createBitmap(this, rect.left, rect.top, rect.width(), rect.height(), null, true)
}

private fun Bitmap.blur(radius: Int = 4): Bitmap {
    return scale(width / radius, height / radius).scale(width, height)
}

private fun provideImageResources(): List<List<ImageResource>> {
    val drawableIdsForTint = listOf(R.drawable.android_adb, R.drawable.amu_bubble_mask, R.drawable.common_full_open_on_phone, R.drawable.ic_shotter, R.drawable.ic_flashlight_on, R.drawable.ic_pause)
    val filter = ColorFilter.tint(Color.Blue)
    return Array(3) { columnIndex ->
        when (columnIndex) {
            0 -> listOf(R.drawable.android_adb, R.drawable.add_to_wallet, R.drawable.common_full_open_on_phone, R.drawable.ic_play)
            1 -> listOf(R.drawable.googleg_standard_color_18, R.drawable.googleg_disabled_color_18, R.drawable.ic_100tb, R.drawable.venom_dead_robot)
            else -> listOf(R.drawable.ic_flashlight_on, R.drawable.ic_flashlight_off, R.drawable.ic_location_pin, R.drawable.ic_pause)
        }.map { ImageResource(it, if (drawableIdsForTint.contains(it)) filter else null) }
    }.toList()
}
