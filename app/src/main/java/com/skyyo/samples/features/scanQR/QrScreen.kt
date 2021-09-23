package com.skyyo.samples.features.scanQR

import android.Manifest
import android.annotation.SuppressLint
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.skyyo.samples.R
import com.skyyo.samples.extensions.goAppPermissions
import com.skyyo.samples.utils.OnClick
import com.skyyo.samples.utils.OnValueChange

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QrScreen(viewModel: QrViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    PermissionRequired(
        permissionState = cameraPermissionState,
        permissionNotGrantedContent = {
            CameraPermissionNotGrantedContent(
                R.string.please_grant_camera_permission_qr,
                cameraPermissionState::launchPermissionRequest
            )
        },
        permissionNotAvailableContent = {
            CameraPermissionNotAvailableContent(
                R.string.please_grant_camera_permission_from_settings,
                context::goAppPermissions
            )
        },
        content = {
            QrCameraPreview(
                modifier = Modifier.fillMaxSize(),
                onQrCodeRecognized = viewModel::onQrCodeRecognized
            )
        }
    )
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun QrCameraPreview(
    modifier: Modifier = Modifier,
    onQrCodeRecognized: OnValueChange
) {
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
    val imageCapture = remember {
        ImageCapture.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .build()
            .also {
                it.setAnalyzer(
                    executor,
                    QrCodeAnalyzer { qrCode -> onQrCodeRecognized(qrCode) }
                )
            }
    }
    val cameraSelector = remember { CameraSelector.DEFAULT_BACK_CAMERA }
    DisposableEffect(Unit) {
        val cameraProvider = cameraProviderFeature.get()

        cameraProviderFeature.addListener(
            {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalysis
                )
            },
            executor
        )

        onDispose { cameraProvider.unbindAll() }
    }

    AndroidView({ cameraPreview }, modifier)
}

@Composable
fun CameraPermissionNotGrantedContent(explanationStringId: Int, onClick: OnClick) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(explanationStringId),
            color = Color.White,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onClick) {
            Text(
                text = "Grant permission",
                color = Color.White,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CameraPermissionNotAvailableContent(explanationStringId: Int, onClick: OnClick) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(explanationStringId),
            color = Color.White,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onClick) {
            Text(
                text = "Grant permission",
                color = Color.White,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
