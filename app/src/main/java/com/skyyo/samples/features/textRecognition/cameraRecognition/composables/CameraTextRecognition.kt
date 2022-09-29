package com.skyyo.samples.features.textRecognition.cameraRecognition.composables

import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.viewinterop.AndroidView
import com.skyyo.samples.features.textRecognition.cameraRecognition.RecognizedSurfaceData
import com.skyyo.samples.utils.scaleBetweenBounds

@Composable
@OptIn(ExperimentalTextApi::class)
fun CameraTextRecognition(
    cameraPreview: PreviewView,
    recognizedSurfaceData: RecognizedSurfaceData?
) {
    val textMeasurer = rememberTextMeasurer()
    Box(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(factory = { cameraPreview }, modifier = Modifier.fillMaxSize())
        Canvas(
            modifier = Modifier.fillMaxSize().clipToBounds(),
            onDraw = {
                recognizedSurfaceData?.let { surfaceData ->
                    val surfaceMinX = 0f
                    val surfaceMaxX = surfaceData.size.width.toFloat()
                    val surfaceMinY = 0f
                    val surfaceMaxY = surfaceData.size.height.toFloat()
                    val canvasMinX = 0f
                    val canvasMaxX = this.size.width
                    val canvasMinY = 0f
                    val canvasMaxY = this.size.height
                    surfaceData.positionedTextList?.forEach { positionedText ->
                        val positionedTextRealX =
                            positionedText.boundsRect.left.toFloat().scaleBetweenBounds(
                                surfaceMinX,
                                surfaceMaxX,
                                canvasMinX,
                                canvasMaxX
                            )
                        val positionedTextRealY =
                            positionedText.boundsRect.exactCenterY().scaleBetweenBounds(
                                surfaceMinY,
                                surfaceMaxY,
                                canvasMinY,
                                canvasMaxY
                            )
                        drawText(
                            textMeasurer = textMeasurer,
                            text = positionedText.text,
                            topLeft = Offset(
                                x = positionedTextRealX,
                                y = positionedTextRealY
                            )
                        )
                    }
                }
            }
        )
    }
}
