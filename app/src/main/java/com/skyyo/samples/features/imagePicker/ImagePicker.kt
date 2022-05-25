package com.skyyo.samples.features.imagePicker

import android.Manifest.permission.READ_EXTERNAL_STORAGE
//import android.Manifest.permission.READ_MEDIA_IMAGES
import android.os.Build
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImagePicker() {
    val pickSingleImageRequest = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {}
    val pickMultipleImagesRequest = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(3)) {}
    val permissionState =
        /*if (Build.VERSION.SDK_INT >= 33) {
            rememberPermissionState(permission = READ_MEDIA_IMAGES)
        } else {*/
            rememberPermissionState(permission = READ_EXTERNAL_STORAGE)
        //}
    ProvideWindowInsets {
        Column(
            Modifier
                .systemBarsPadding()
                .padding(20.dp)
        ) {
            PermissionRequired(
                permissionState = permissionState,
                permissionNotGrantedContent = {
                    Button(onClick = { permissionState.launchPermissionRequest() }) {
                        Text(text = "Grant permission")
                    }
                },
                permissionNotAvailableContent = { /*TODO*/ },
                content = {
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
                })
        }
    }
}