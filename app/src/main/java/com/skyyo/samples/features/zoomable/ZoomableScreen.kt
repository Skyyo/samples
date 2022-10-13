package com.skyyo.samples.features.zoomable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ZoomableScreen() {
    val context = LocalContext.current
    Zoomable {
        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context)
                .data("https://cataas.com/cat?type=or")
                .size(Size.ORIGINAL)
                .build()
        )
        if (painter.state is AsyncImagePainter.State.Success) {
            val size = painter.intrinsicSize
            Image(
                painter = painter, contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .aspectRatio(size.width / size.height)
                    .fillMaxSize()
            )
        } else {
            CircularProgressIndicator()
        }
    }
}
