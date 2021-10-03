package com.skyyo.samples.features.exoPlayer.columnDynamicThumb

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.request.videoFrameMillis

@OptIn(ExperimentalCoilApi::class)
@Composable
fun DynamicVideoThumbnail(
    imageLoader: ImageLoader,
    mediaUrl: String,
    lastPlayedPosition: Long
) {
    val painter = rememberImagePainter(
        data = mediaUrl,
        imageLoader = imageLoader,
        builder = {
            videoFrameMillis(lastPlayedPosition)
            crossfade(true)
            size(512, 512)
            placeholder(android.R.drawable.ic_delete)
        },
    )
    Image(
        painter = painter,
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