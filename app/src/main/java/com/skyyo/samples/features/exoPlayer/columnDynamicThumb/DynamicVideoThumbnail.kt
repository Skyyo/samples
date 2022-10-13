package com.skyyo.samples.features.exoPlayer.columnDynamicThumb

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.request.videoFrameMillis

@Composable
fun DynamicVideoThumbnail(
    imageLoader: ImageLoader,
    mediaUrl: String,
    lastPlayedPosition: Long
) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(mediaUrl)
            .videoFrameMillis(lastPlayedPosition)
            .crossfade(true)
            .size(512)
            .placeholder(android.R.drawable.ic_delete)
            .build(),
        imageLoader = imageLoader
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
