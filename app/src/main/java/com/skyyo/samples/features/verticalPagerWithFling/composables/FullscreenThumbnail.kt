package com.skyyo.samples.features.verticalPagerWithFling.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.skyyo.samples.utils.rememberFilePainter

@Composable
fun FullscreenVideoThumbnail(filePath: String) {
    Image(
        painter = rememberFilePainter(filePath = filePath),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}
