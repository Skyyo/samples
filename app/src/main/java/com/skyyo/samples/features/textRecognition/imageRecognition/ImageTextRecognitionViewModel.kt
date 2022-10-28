package com.skyyo.samples.features.textRecognition.imageRecognition

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

private const val IMAGE_URI = "imageUri"
private const val PARSED_TEXT = "parsedText"
private const val IS_STATIC_IMAGE_RECOGNITION_IN_PROGRESS =
    "isTextFromImageRecognitionInProgress"

@HiltViewModel
class TextRecognitionViewModel @Inject constructor(
    private val handle: SavedStateHandle
) : ViewModel() {

    private val _events = Channel<ImageTextRecognitionEvent>(Channel.UNLIMITED)
    val events = _events.receiveAsFlow()
    val isStaticImageRecognitionInProgress = handle.getStateFlow(
        IS_STATIC_IMAGE_RECOGNITION_IN_PROGRESS,
        false
    )
    val documentImageUri = handle.getStateFlow<Uri?>(IMAGE_URI, null)
    val parsedText = handle.getStateFlow<String?>(PARSED_TEXT, null)

    fun onTextParsed(text: String) {
        handle[PARSED_TEXT] = text
        handle[IS_STATIC_IMAGE_RECOGNITION_IN_PROGRESS] = false
    }

    fun onImagePicked(uri: Uri?) {
        handle[IMAGE_URI] = uri
        uri?.let {
            _events.trySend(ImageTextRecognitionEvent.RecogniseImageTextByUri(it))
            handle[IS_STATIC_IMAGE_RECOGNITION_IN_PROGRESS] = true
        }
    }
}
