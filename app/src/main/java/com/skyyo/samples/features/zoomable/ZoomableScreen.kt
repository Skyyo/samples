package com.skyyo.samples.features.zoomable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.size.OriginalSize

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ZoomableScreen() {
    Zoomable {
        val painter = rememberImagePainter(
            data = "https://cataas.com/cat?type=or"
        ) { size(OriginalSize) }
        if (painter.state is ImagePainter.State.Success) {
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
