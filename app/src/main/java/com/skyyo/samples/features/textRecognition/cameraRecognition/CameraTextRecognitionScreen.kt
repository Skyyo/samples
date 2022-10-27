package com.skyyo.samples.features.textRecognition.cameraRecognition

import android.Manifest
import android.util.Size
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.skyyo.samples.extensions.goAppPermissions
import com.skyyo.samples.extensions.tryOrNull
import com.skyyo.samples.features.textRecognition.cameraRecognition.composables.CameraTextRecognition
import java.util.concurrent.Executors

@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalLifecycleComposeApi::class
)
@Composable
fun CameraTextRecognitionScreen(viewModel: CameraTextRecognitionViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val recognizedSurfaceData by viewModel.recognizedSurfaceData.collectAsStateWithLifecycle()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    PermissionRequired(
        permissionState = cameraPermissionState,
        permissionNotGrantedContent = {
            Button(
                onClick = { cameraPermissionState.launchPermissionRequest() },
                modifier = Modifier.systemBarsPadding()
            ) {
                Text(text = "Add camera permission")
            }
        },
        permissionNotAvailableContent = {
            Button(onClick = { context.goAppPermissions() }, Modifier.systemBarsPadding()) {
            Text(text = "Add camera permission from app Settings")
        }
        }
    ) {
        val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
        val imageCapture = remember {
            ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
        }
        val imageAnalyzer = remember {
            ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(
                        cameraExecutor,
                        TextImageAnalyzer(viewModel::onSurfaceAnalyzed)
                    )
                }
        }
        val cameraPreview = remember {
            PreviewView(context).apply {
                id = View.generateViewId()
                scaleType = PreviewView.ScaleType.FIT_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        }
        val cameraProvider by produceState<ProcessCameraProvider?>(null) {
            value = tryOrNull { ProcessCameraProvider.getInstance(context).get() }
        }

        DisposableEffect(Unit) {
            onDispose {
                cameraProvider?.unbindAll()
                cameraExecutor.shutdown()
            }
        }

        remember(cameraProvider) {
            cameraProvider?.apply {
                unbindAll()
                bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    Preview.Builder()
                        .setTargetResolution(Size(cameraPreview.width, cameraPreview.height))
                        .build().also { preview ->
                            preview.setSurfaceProvider(cameraPreview.surfaceProvider)
                        },
                    imageCapture,
                    imageAnalyzer
                )
            }
        }
        CameraTextRecognition(cameraPreview, recognizedSurfaceData)
    }
}
