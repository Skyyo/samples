package com.skyyo.samples.features.textSpans

import android.graphics.Region
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.graphics.forEach
import kotlin.math.ceil
import kotlin.math.sin

private const val SEGMENTS_PER_WAVELENGTH = 10

fun Path.buildSquigglesFor(
    box: Rect,
    density: Density,
    waveOffset: Float = 0f,
    waveLength: TextUnit = 10.sp,
    width: TextUnit = 2.sp,
    amplitude: TextUnit = 1.sp,
    bottomOffset: TextUnit = 1.sp
) = density.run {
    val twoPi = Math.PI * 2
    val lineStart = box.left + width.toPx() / 2
    val lineEnd = box.right - width.toPx() / 2
    val lineBottom = box.bottom + bottomOffset.toPx()

    val segmentWidth = waveLength.toPx() / SEGMENTS_PER_WAVELENGTH
    val numOfPoints = ceil((lineEnd - lineStart) / segmentWidth).toInt() + 1

    var pointX = lineStart
    for (point in 0..numOfPoints) {
        val proportionOfWavelength = (pointX - lineStart) / waveLength.toPx()
        val radiansX = proportionOfWavelength * twoPi + twoPi * waveOffset
        val offsetY = lineBottom + (sin(radiansX) * amplitude.toPx()).toFloat()

        when (point) {
            0 -> moveTo(pointX, offsetY)
            else -> lineTo(pointX, offsetY)
        }
        pointX = (pointX + segmentWidth).coerceAtMost(lineEnd)
    }
}

fun TextLayoutResult.getBoundingBoxes(
    region: Region,
    startOffset: Int,
    endOffset: Int,
): List<Rect> {
    val annotationPath = getPathForRange(startOffset, endOffset)
    region.setPath(annotationPath.asAndroidPath(), Region(annotationPath.getBounds().toAndroidRect()))
    val boundingBoxes = mutableListOf<Rect>()
    region.forEach { rect -> boundingBoxes.add(rect.toComposeRect()) }
    return boundingBoxes
}
