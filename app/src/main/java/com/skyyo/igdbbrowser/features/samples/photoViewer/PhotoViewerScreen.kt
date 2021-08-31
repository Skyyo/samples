package com.skyyo.igdbbrowser.features.samples.photoViewer

import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore.Images.Media.getBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PhotoScreen(viewerViewModel: PhotoViewerViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val imageUri by viewerViewModel.photoUri.collectAsState()
    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, imageUri))
    } else {
        getBitmap(context.contentResolver, imageUri)
    }
    Image(
        painter = BitmapPainter(bitmap.asImageBitmap()),
        contentDescription = "",
        modifier = Modifier.fillMaxSize()
    )
}