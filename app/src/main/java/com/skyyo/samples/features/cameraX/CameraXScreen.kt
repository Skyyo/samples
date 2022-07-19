package com.skyyo.samples.features.cameraX

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.View
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.FLASH_MODE_OFF
import androidx.camera.core.ImageCapture.FLASH_MODE_ON
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
import com.skyyo.samples.R
import com.skyyo.samples.extensions.goAppPermissions
import com.skyyo.samples.extensions.toast
import com.skyyo.samples.extensions.tryOrNull
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraXScreen(viewModel: CameraXViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var isFlashlightOn by remember { mutableStateOf(false) }
    var isAlwaysOnFlashLightEnabled by remember { mutableStateOf(false) }
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
                isAlwaysOnFlashLightEnabled = isAlwaysOnFlashLightEnabled,
                modifier = Modifier.fillMaxSize(),
                imageCapture = imageCapture
            )
            ToggleFlashlightButton(
                isFlashlightOn = isFlashlightOn,
                modifier = Modifier
                    .padding(16.dp)
                    .navigationBarsPadding()
                    .size(48.dp)
                    .clickable {
                        imageCapture.flashMode = when {
                            isFlashlightOn -> FLASH_MODE_OFF
                            else -> FLASH_MODE_ON
                        }
                        isFlashlightOn = !isFlashlightOn
                    }
                    .align(BottomStart)
            )
            ToggleFlashlightButton(
                isFlashlightOn = isFlashlightOn,
                modifier = Modifier
                    .padding(16.dp)
                    .navigationBarsPadding()
                    .size(48.dp)
                    .background(Color.Magenta)
                    .clickable {
                        isAlwaysOnFlashLightEnabled = !isAlwaysOnFlashLightEnabled
                    }
                    .align(BottomEnd)
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
            TakePictureButton(
                Modifier
                    .navigationBarsPadding()
                    .size(76.dp)
                    .clip(RoundedCornerShape(76.dp))
                    .clickable { takePhoto(imageCapture, context, viewModel, cameraExecutor) }
                    .align(BottomCenter)
            )
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CameraPreview(
    isAlwaysOnFlashLightEnabled: Boolean,
    modifier: Modifier = Modifier,
    imageCapture: ImageCapture,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraPreview = remember {
        PreviewView(context).apply {
            id = View.generateViewId()
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
    val cameraProvider by produceState<ProcessCameraProvider?>(null) {
        value = tryOrNull { ProcessCameraProvider.getInstance(context).get() }
    }
    val camera = remember(cameraProvider) {
        cameraProvider?.let { camProvider ->
            camProvider.unbindAll()
            camProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .build().also { preview ->
                        preview.setSurfaceProvider(cameraPreview.surfaceProvider)
                    },
                imageCapture
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose { cameraProvider?.unbindAll() }
    }

    LaunchedEffect(isAlwaysOnFlashLightEnabled) {
        camera?.let {
            if (it.cameraInfo.hasFlashUnit()) {
                it.cameraControl.enableTorch(isAlwaysOnFlashLightEnabled)
            }
        }
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

@Composable
fun ToggleFlashlightButton(isFlashlightOn: Boolean, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = if (isFlashlightOn) R.drawable.ic_flashlight_on else R.drawable.ic_flashlight_off),
        contentDescription = "",
        modifier = modifier
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun LastPictureThumbnail(
    uri: Uri?,
    modifier: Modifier = Modifier,
) {
    Crossfade(targetState = uri, animationSpec = tween(durationMillis = 500)) { imageUri ->
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
    cameraExecutor: ExecutorService,
) {
    val folderName = context.getString(R.string.app_name)
    val mediaDir = context.filesDir?.let { File(it, folderName).apply { mkdirs() } }
    val outputDirectory: File = when {
        mediaDir != null && mediaDir.exists() -> mediaDir
        else -> context.filesDir
    }
    val photoFile = File(outputDirectory, "${System.currentTimeMillis()}test.jpg")
    val options = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        options, cameraExecutor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                context.toast("Failed: ${exc.message}")
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                viewModel.onPictureTaken(output.savedUri ?: Uri.fromFile(photoFile))
            }
        }
    )
}
