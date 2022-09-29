package com.skyyo.samples.features.textRecognition.imageRecognition

import android.net.Uri

sealed class ImageTextRecognitionEvent {
    class RecogniseImageTextByUri(val uri: Uri) : ImageTextRecognitionEvent()
}
