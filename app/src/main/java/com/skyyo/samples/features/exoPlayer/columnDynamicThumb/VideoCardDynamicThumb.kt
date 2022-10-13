package com.skyyo.samples.features.exoPlayer.columnDynamicThumb

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import coil.ImageLoader
import com.skyyo.samples.R
import com.skyyo.samples.features.exoPlayer.VideoPlayer
import com.skyyo.samples.features.exoPlayer.common.VideoItemImmutable
import com.skyyo.samples.theme.Shapes
import com.skyyo.samples.utils.OnClick

@Composable
fun VideoCardIndexed(
    modifier: Modifier = Modifier,
    videoItem: VideoItemImmutable,
    imageLoader: ImageLoader,
    isPlaying: Boolean,
    onClick: OnClick,
    exoPlayer: ExoPlayer
) {
    var isPlayerUiVisible by remember { mutableStateOf(false) }
    val isPlayButtonVisible = if (isPlayerUiVisible) true else !isPlaying

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black, Shapes.medium)
            .clip(Shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        if (isPlaying) {
            VideoPlayer(exoPlayer) { uiVisible ->
                isPlayerUiVisible = if (isPlayerUiVisible) uiVisible else true
            }
        } else {
            DynamicVideoThumbnail(imageLoader, videoItem.mediaUrl, videoItem.lastPlayedPosition)
        }
        if (isPlayButtonVisible) {
            Icon(
                painter = painterResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(72.dp)
                    .clickable(onClick = onClick)
            )
        }
        Text(
            text = "${videoItem.id}",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        )
    }
}
