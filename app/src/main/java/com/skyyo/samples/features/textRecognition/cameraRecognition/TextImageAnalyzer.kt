package com.skyyo.samples.features.textRecognition.cameraRecognition

import android.media.Image
import android.util.Log
import android.util.Size
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException

private const val CAPTURING_IMAGE_ANGLE = 90

class TextImageAnalyzer(
    val onTextFound: (RecognizedSurfaceData) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        image.image?.let { process(it, image) }
    }

    private fun process(image: Image, imageProxy: ImageProxy) {
        try {
            readTextFromImage(InputImage.fromMediaImage(image, CAPTURING_IMAGE_ANGLE), imageProxy)
        } catch (e: IOException) {
            Log.d("vitalik", "Failed to load image")
            e.printStackTrace()
        }
    }

    private fun readTextFromImage(image: InputImage, imageProxy: ImageProxy) {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            .process(image)
            .addOnSuccessListener { visionText ->
                processTextFromImage(visionText, Size(image.width, image.height))
                imageProxy.close()
            }
            .addOnFailureListener { e ->
                Log.d("vitalik", "Failed to read text from image")
                e.printStackTrace()
                imageProxy.close()
            }
    }

    private fun processTextFromImage(visionText: Text, inputImageSize: Size) {
        val positionedText = mutableListOf<PositionedText>()
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                line.boundingBox?.let { box ->
                    positionedText.add(PositionedText(line.text, box))
                }
            }
        }
        onTextFound(RecognizedSurfaceData(inputImageSize, positionedText))
    }
}
