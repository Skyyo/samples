package com.skyyo.samples.features.verticalPagerWithFling.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter

@OptIn(ExperimentalCoilApi::class)
@Composable
fun FullscreenVideoThumbnail(url: String) {
    Image(
        painter = rememberImagePainter(data = url, builder = {
            crossfade(true)
            size(512, 512)
        }),
        contentDescription = null,
        modifier = Modifier
            // this can be optimized to prevent overdraw. Should be shown only
            // when loading is in progress
//            .background(Color.Black)
            .fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}