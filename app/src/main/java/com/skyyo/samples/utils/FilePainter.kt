package com.skyyo.samples.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
@Composable
fun rememberFilePainter(filePath: String, colorFilter: ColorFilter? = null): Painter {
    return remember { FilePainter(filePath) }.apply {
        // These assignments are thread safe as parameters are backed by a mutableState object
        this.intrinsicColorFilter = colorFilter
        DisposableEffect(Unit) {
            onDispose { bitmap.recycle() }
        }
    }
}

private class FilePainter(filePath: String) : Painter() {
    val bitmap: Bitmap = BitmapFactory.decodeFile(filePath)
    /**
     * configures the intrinsic tint that may be defined on a VectorPainter
     */
    var intrinsicColorFilter: ColorFilter? by mutableStateOf(null)

    private var currentAlpha: Float = 1.0f
    private var currentColorFilter: ColorFilter? = null

    override val intrinsicSize: Size = Size(bitmap.width.toFloat(), bitmap.height.toFloat())

    override fun DrawScope.onDraw() {
        val filter = currentColorFilter ?: intrinsicColorFilter
        drawImage(bitmap.asImageBitmap(), alpha = currentAlpha, colorFilter = filter)
    }

    override fun applyAlpha(alpha: Float): Boolean {
        currentAlpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        currentColorFilter = colorFilter
        return true
    }
}
