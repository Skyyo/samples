package com.skyyo.samples.features.textRecognition.imageRecognition

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.skyyo.samples.extensions.toast
import com.skyyo.samples.features.textRecognition.imageRecognition.composables.CopyableText
import com.skyyo.samples.features.textRecognition.imageRecognition.composables.ImageContentFrame
import com.skyyo.samples.features.textRecognition.imageRecognition.composables.RecognitionProgressIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val IMAGE_URI_FILTER = "image/*"

@OptIn(ExperimentalAnimationApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun ImageTextRecognitionScreen(viewModel: TextRecognitionViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val pickedImageUri by viewModel.documentImageUri.collectAsStateWithLifecycle()
    val parsedText by viewModel.parsedText.collectAsStateWithLifecycle()
    val isStaticImageRecognitionInProgress by viewModel.isStaticImageRecognitionInProgress.collectAsStateWithLifecycle()
    val pickedImageBitmap = remember(pickedImageUri) {
        pickedImageUri?.let { return@remember context.getBitmapFromUri(it).asImageBitmap() }
        return@remember null
    }
    val textRecognizer = remember {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> viewModel.onImagePicked(uri) }
    )
    val events = remember(viewModel.events, lifecycleOwner) {
        viewModel.events.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
    }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is ImageTextRecognitionEvent.RecogniseImageTextByUri -> {
                    launch(Dispatchers.IO) {
                        val inputImage = InputImage.fromFilePath(context, event.uri)
                        textRecognizer.process(inputImage)
                            .addOnSuccessListener { visionText ->
                                viewModel.onTextParsed(visionText.text)
                            }
                            .addOnFailureListener { e ->
                                context.toast(e.message ?: "Unknown exception occurred!")
                            }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImageContentFrame(
            onClick = { imagePickerLauncher.launch(IMAGE_URI_FILTER) },
            imageBitmap = pickedImageBitmap
        )
        AnimatedContent(
            targetState = isStaticImageRecognitionInProgress
        ) { isImageRecognitionInProgress ->
            when (isImageRecognitionInProgress) {
                true -> RecognitionProgressIndicator()
                false -> parsedText?.let { CopyableText(it) }
            }
        }
    }
}

@Suppress("DEPRECATION")
private fun Context.getBitmapFromUri(uri: Uri): Bitmap {
    val bitmap = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        MediaStore.Images.Media.getBitmap(contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
    return bitmap
}
