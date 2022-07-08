@file:JvmName("FullscreenVideoPlayerKt")

package com.skyyo.samples.features.verticalPagerWithFling

import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.skyyo.samples.features.exoPlayer.common.VideoItem
import com.skyyo.samples.utils.OnClick

@Composable
fun VerticalPagerWithFlingPage(
    modifier: Modifier = Modifier,
    videoItem: VideoItem,
    isPlaying: Boolean,
    isThumbnailVisible: Boolean,
    exoPlayer: ExoPlayer,
    onClick: OnClick
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isPlaying) FullscreenVideoPlayer(exoPlayer)
        if (isThumbnailVisible) FullscreenVideoThumbnail(videoItem.thumbnail)
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "${videoItem.id}",
            fontSize = 42.sp,
            color = Color.Red,
            fontWeight = FontWeight.W700
        )
    }
}

@Composable
private fun FullscreenVideoPlayer(exoPlayer: ExoPlayer) {
    val context = LocalContext.current
    val playerView = remember {
        PlayerView(context).apply {
            player = exoPlayer
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
//            setBackgroundColor(Color.Blue.toArgb())
            // TODO for testing purpose
            setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
//            setKeepContentOnPlayerReset(true)
            setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }
    }

//    val surfaceView = remember {
//        SurfaceView(context)
//    }
//    exoPlayer.setVideoSurfaceView(surfaceView)
//    exoPlayer.clearVideoSurface()

    AndroidView(
        factory = { playerView },
        modifier = Modifier.fillMaxSize()
    ) {
//        exoPlayer.clearVideoSurface()
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun FullscreenVideoThumbnail(url: String) {
    Image(
        painter = rememberImagePainter(data = url, builder = {
            crossfade(true)
            size(width = 1080, height = 1980)
        }),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}
