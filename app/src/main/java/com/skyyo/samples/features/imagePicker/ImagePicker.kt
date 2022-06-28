package com.skyyo.samples.features.imagePicker

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ProvideWindowInsets

@Composable
fun ImagePickerScreen() {
    val pickSingleImageRequest =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            Log.d("ImagePicker", "Single image uri: $it")
        }
    val pickMultipleImagesRequest =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) {
            Log.d("ImagePicker", "Multiple image uri's: $it")
        }
    ProvideWindowInsets {
        Column(
            Modifier
                .systemBarsPadding()
                .padding(20.dp)
        ) {
            Button(onClick = {
                pickSingleImageRequest.launch(PickVisualMediaRequest(ImageOnly))
            }) {
                Text(text = "Pick single image")
            }
            Button(onClick = {
                pickMultipleImagesRequest.launch(PickVisualMediaRequest(ImageOnly))
            }) {
                Text(text = "Pick multiple")
            }
        }
    }
}
