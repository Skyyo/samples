package com.skyyo.samples.features.verticalPagerWithFling.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.android.exoplayer2.ExoPlayer
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
            FullscreenVideoPlayer(exoPlayer)
        } else {
            FullscreenVideoThumbnail(videoItem.thumbnail)
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