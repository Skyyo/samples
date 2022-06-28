package com.skyyo.samples.features.verticalPagerWithFling.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView

@Composable
fun FullscreenVideoPlayer(exoPlayer: ExoPlayer) {
    val context = LocalContext.current
    val styledPlayerView = remember {
        StyledPlayerView(context).apply {
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            player = exoPlayer
        }
    }

    AndroidView(
        factory = { styledPlayerView },
        modifier = Modifier.fillMaxSize()
    )
}
