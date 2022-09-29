package com.skyyo.samples.features.textRecognition.cameraRecognition

import android.graphics.Rect
import android.util.Size
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

data class PositionedText(val text: String, val boundsRect: Rect)
data class RecognizedSurfaceData(val size: Size, val positionedTextList: List<PositionedText>?)

@HiltViewModel
class CameraTextRecognitionViewModel @Inject constructor() : ViewModel() {

    val recognizedSurfaceData = MutableStateFlow<RecognizedSurfaceData?>(null)

    fun onSurfaceAnalyzed(recognizedSurfaceData: RecognizedSurfaceData) {
        this.recognizedSurfaceData.value = recognizedSurfaceData
    }
}
