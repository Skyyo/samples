package com.skyyo.samples.features.signatureView

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@Composable
fun SignatureViewScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val signatureEvents = remember { Channel<SignatureViewEvent>(Channel.UNLIMITED) }
    val signatureEventsFlow = remember(signatureEvents, lifecycleOwner) {
        signatureEvents
            .receiveAsFlow()
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
    }
    val context = LocalContext.current
    val stroke = remember { Stroke(10f) }

    fun saveMediaToStorage(bitmap: Bitmap, imageName: String = "bitmap"): Boolean {
        val saved: Boolean
        val fos: OutputStream?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver: ContentResolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/")
            }

            val imageUri: Uri? =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            ).toString() + File.separator
            val file = File(imagesDir)
            if (!file.exists()) {
                file.mkdir()
            }
            val image = File(imagesDir, "$imageName.png")
            fos = FileOutputStream(image)
        }
        val quality = 100
        saved = bitmap.compress(Bitmap.CompressFormat.PNG, quality, fos) == true
        fos?.flush()
        fos?.close()
        return saved
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        verticalArrangement = Arrangement.Center
    ) {
        SignatureView(
            modifier = Modifier
                .height(200.dp)
                .width(200.dp)
                .align(CenterHorizontally)
                .background(Color.White),
            events = signatureEventsFlow,
            stroke = stroke,
            onBitmapSaved = {
                saveMediaToStorage(it)
            }
        )
        Row(
            Modifier
                .fillMaxWidth()
                .align(CenterHorizontally),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { signatureEvents.trySend(SignatureViewEvent.Save) }) {
                Text(text = "Save")
            }
            Button(onClick = { signatureEvents.trySend(SignatureViewEvent.Reset) }) {
                Text(text = "Reset")
            }
        }
    }
}
