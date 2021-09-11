package com.skyyo.igdbbrowser.features.cameraX

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.skyyo.igdbbrowser.R
import com.skyyo.igdbbrowser.extensions.goAppPermissions
import com.skyyo.igdbbrowser.extensions.toast
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraXScreen(viewModel: CameraXViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    val lastTakenPictureUri by viewModel.latestTakenPictureUri.observeAsState()

    val imageCapture = remember {
        ImageCapture.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    PermissionRequired(
        permissionState = cameraPermissionState,
        permissionNotGrantedContent = {
            Button(
                onClick = { cameraPermissionState.launchPermissionRequest() },
                Modifier.systemBarsPadding(true)
            ) {
                Text(text = "Add camera permission")
            }
        },
        permissionNotAvailableContent = {
            Button(onClick = { context.goAppPermissions() }, Modifier.systemBarsPadding(true)) {
                Text(text = "Add camera permission from app Settings")
            }
        }
    ) {
        Box {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                imageCapture = imageCapture
            )
            LastPictureThumbnail(
                uri = lastTakenPictureUri,
                modifier = Modifier
//                    .fillMaxSize()
                    .padding(16.dp)
                    .size(168.dp)
                    .statusBarsPadding()
                    .align(TopStart)
            )
            TakePictureButton(Modifier
                .navigationBarsPadding()
                .size(76.dp)
                .clip(RoundedCornerShape(76.dp))
                .clickable { takePhoto(imageCapture, context, viewModel, cameraExecutor) }
                .align(BottomCenter))
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CameraPreview(modifier: Modifier = Modifier, imageCapture: ImageCapture) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraPreview = remember {
        PreviewView(context).apply {
            id = View.generateViewId()
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    val cameraProviderFeature = remember { ProcessCameraProvider.getInstance(context) }
    val executor = remember { ContextCompat.getMainExecutor(context) }
    val preview = remember {
        Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
//            .setTargetResolution(Size(1280, 720))
            .build().also {
                it.setSurfaceProvider(cameraPreview.surfaceProvider)
            }
    }
    val cameraSelector = remember { CameraSelector.DEFAULT_BACK_CAMERA }

    DisposableEffect(Unit) {
        val cameraProvider = cameraProviderFeature.get()

        cameraProviderFeature.addListener({
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        }, executor)

        onDispose { cameraProvider.unbindAll() }
    }

    AndroidView({ cameraPreview }, modifier)
}

@Composable
fun TakePictureButton(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_shotter),
        contentDescription = "",
        modifier = modifier
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun LastPictureThumbnail(
    uri: Uri?,
    modifier: Modifier = Modifier
) {
    Crossfade(targetState = uri, animationSpec = tween(500)) { imageUri ->
        val painter = rememberImagePainter(
            data = imageUri,
            builder = {
                transformations(RoundedCornersTransformation(16f))
            }
        )
        Image(
            painter = painter,
            contentDescription = null,
            modifier = modifier
        )
    }
}

private fun takePhoto(
    imageCapture: ImageCapture,
    context: Context,
    viewModel: CameraXViewModel,
    cameraExecutor: ExecutorService
) {
    val folderName = context.getString(R.string.app_name)
    val mediaDir = context.filesDir?.let { File(it, folderName).apply { mkdirs() } }
    val outputDirectory: File = when {
        mediaDir != null && mediaDir.exists() -> mediaDir
        else -> context.filesDir
    }
    val photoFile = File(outputDirectory, "${System.currentTimeMillis()}test.jpg")
    val options = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(options, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
        override fun onError(exc: ImageCaptureException) {
            context.toast("Failed: ${exc.message}")
        }

        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
            viewModel.onPictureTaken(output.savedUri ?: Uri.fromFile(photoFile))
        }
    })
}

