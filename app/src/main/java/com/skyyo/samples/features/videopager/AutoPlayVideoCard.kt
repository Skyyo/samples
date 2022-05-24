package com.skyyo.samples.features.videopager

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.skyyo.samples.features.exoPlayer.common.VideoItem

@Composable
fun AutoPlayVideoCard(
    modifier: Modifier = Modifier,
    videoItem: VideoItem,
    isPlaying: Boolean,
    exoPlayer: ExoPlayer,
) {
    Box(
        modifier = modifier.background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (isPlaying) {
            VideoPlayer(exoPlayer)
        } else {
            VideoThumbnail(videoItem.thumbnail)
        }
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
fun VideoPlayer(exoPlayer: ExoPlayer) {
    val context = LocalContext.current
    
    AndroidView(
        factory = {
            StyledPlayerView(context).apply {
                useController = true
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                player = exoPlayer
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun VideoThumbnail(url: String) {
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