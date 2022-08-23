package com.skyyo.samples.features.textSpans

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.ResolvedTextDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
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
    fastMapRange(0, numOfPoints) { point ->
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
    startOffset: Int,
    endOffset: Int,
    flattenForFullParagraphs: Boolean = false
): List<Rect> {
    if (startOffset == endOffset) {
        return emptyList()
    }

    val startLineNum = getLineForOffset(startOffset)
    val endLineNum = getLineForOffset(endOffset)

    if (flattenForFullParagraphs) {
        val isFullParagraph = startLineNum != endLineNum &&
            getLineStart(startLineNum) == startOffset &&
            multiParagraph.getLineEnd(endLineNum, visibleEnd = true) == endOffset

        if (isFullParagraph) {
            return listOf(
                Rect(
                    top = getLineTop(startLineNum),
                    bottom = getLineBottom(endLineNum),
                    left = 0f,
                    right = size.width.toFloat()
                )
            )
        }
    }
    // Compose UI does not offer any API for reading paragraph direction for an entire line.
    // So this code assumes that all paragraphs in the text will have the same direction.
    // It also assumes that this paragraph does not contain bi-directional text.
    val isLtr =
        multiParagraph.getParagraphDirection(offset = layoutInput.text.lastIndex) == ResolvedTextDirection.Ltr

    return fastMapRange(startLineNum, endLineNum) { lineNum ->
        Rect(
            top = getLineTop(lineNum),
            bottom = getLineBottom(lineNum),
            left = if (lineNum == startLineNum) {
                getHorizontalPosition(startOffset, usePrimaryDirection = isLtr)
            } else {
                getLineLeft(lineNum)
            },
            right = if (lineNum == endLineNum) {
                getHorizontalPosition(endOffset, usePrimaryDirection = isLtr)
            } else {
                getLineRight(lineNum)
            }
        )
    }
}

@OptIn(ExperimentalContracts::class)
internal inline fun <R> fastMapRange(
    start: Int,
    end: Int,
    transform: (Int) -> R
): List<R> {
    contract { callsInPlace(transform) }
    val destination = ArrayList<R>(/* initialCapacity = */ end - start + 1)
    for (i in start..end) {
        destination.add(transform(i))
    }
    return destination
}
