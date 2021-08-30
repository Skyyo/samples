package com.skyyo.igdbbrowser.features.samples.cameraX

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.skyyo.igdbbrowser.R
import com.skyyo.igdbbrowser.extensions.goAppPermissions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@ExperimentalPermissionsApi
@Composable
fun CameraXScreen(viewModel: CameraXViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
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
        }) {
        val cameraPreview = remember {
            PreviewView(context).apply {
                id = View.generateViewId()
            }
        }
        val cameraProviderFeature = remember { ProcessCameraProvider.getInstance(context) }
        val executor = remember { ContextCompat.getMainExecutor(context) }
        val preview = remember {
            Preview.Builder().build().also {
                it.setSurfaceProvider(cameraPreview.surfaceProvider)
            }
        }
        val cameraSelector = remember {
            CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
        }
        val imageCapture = remember {
            ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()
        }
        DisposableEffect(Unit) {
            val cameraProvider = cameraProviderFeature.get()
            cameraProviderFeature.addListener({
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            }, executor)
            onDispose {  cameraProvider.unbindAll() }
        }
        Box(contentAlignment = Alignment.BottomCenter) {
            AndroidView({ cameraPreview }, Modifier.fillMaxSize())
            Image(
                painter = painterResource(id = R.drawable.ic_shotter),
                contentDescription = "",
                Modifier
                    .width(75.dp)
                    .height(75.dp)
                    .padding(bottom = 15.dp)
                    .clickable { takePhoto(imageCapture, context, viewModel) }
            )
        }
    }
}

private fun takePhoto(imageCapture: ImageCapture, context: Context, viewModel: CameraXViewModel) {
    imageCapture.let {
        val outputDirectory: File by lazy {
            val folderName = context.getString(R.string.app_name)
            val mediaDir = context.filesDir?.let { File(it, folderName).apply { mkdirs() } }
            if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
        }
        val cameraExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }

        val photoFile = File(outputDirectory, "test.jpg")
        val options = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        it.takePicture(
            options, cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("err", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    val bitmap = BitmapFactory.decodeFile(savedUri.path)
                    val result = ByteArrayOutputStream().use {
                        bitmap.rotate(90f).compress(Bitmap.CompressFormat.JPEG, 100, it)
                        it.toByteArray()
                    }
                    FileOutputStream(photoFile).use { fos ->
                        fos.write(result)
                        fos.flush()
                    }
                    viewModel.goPhoto(savedUri)
                    cameraExecutor.shutdown()
                }
            }
        )
    }
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}