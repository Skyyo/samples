package com.skyyo.samples.features.exoPlayer.columnAutoplay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import com.skyyo.samples.features.exoPlayer.VideoPlayerWithControls
import com.skyyo.samples.features.exoPlayer.common.VideoItem
import com.skyyo.samples.features.exoPlayer.common.VideoThumbnail
import com.skyyo.samples.theme.Shapes

@Composable
fun AutoPlayVideoCard(
    videoItem: VideoItem,
    isPlaying: Boolean,
    exoPlayer: ExoPlayer,
) {
    Box(
        modifier = Modifier
            .height(256.dp)
            .fillMaxWidth()
            .background(Color.Black, Shapes.medium)
            .clip(Shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        if (isPlaying) {
            VideoPlayerWithControls(exoPlayer)
        } else {
            VideoThumbnail(videoItem.thumbnail)
        }
        Text(
            text = "${videoItem.id}",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        )
    }
}
