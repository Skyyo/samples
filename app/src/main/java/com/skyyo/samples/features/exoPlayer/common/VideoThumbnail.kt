package com.skyyo.samples.features.exoPlayer.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@OptIn(ExperimentalCoilApi::class)
@Composable
fun VideoThumbnail(url: String) {
    val context = LocalContext.current
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context)
                .data(url)
                .crossfade(true)
                .size(512)
                .build()
        ),
        contentDescription = null,
        modifier = Modifier
            // this can be optimized to prevent overdraw. Should be shown only
            // when loading is in progress
//            .background(Color.Black)
            .fillMaxWidth()
            .size(256.dp),
        contentScale = ContentScale.Crop
    )
}
