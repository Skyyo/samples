package com.skyyo.samples.features.exoPlayer.columnDynamicThumb

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import com.google.android.exoplayer2.SimpleExoPlayer
import com.skyyo.samples.R
import com.skyyo.samples.features.exoPlayer.VideoPlayer
import com.skyyo.samples.features.exoPlayer.common.VideoItemImmutable
import com.skyyo.samples.theme.Shapes
import com.skyyo.samples.utils.OnClick

@OptIn(ExperimentalCoilApi::class)
@Composable
fun VideoCardIndexed(
    modifier: Modifier = Modifier,
    videoItem: VideoItemImmutable,
    imageLoader: ImageLoader,
    isPlaying: Boolean,
    onClick: OnClick,
    exoPlayer: SimpleExoPlayer
) {
    val isPlayerUiVisible = remember { mutableStateOf(false) }
    val isPlayButtonVisible = if (isPlayerUiVisible.value) true else !isPlaying

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black, Shapes.medium)
            .clip(Shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        if (isPlaying) {
            VideoPlayer(exoPlayer) { uiVisible ->
                if (isPlayerUiVisible.value) {
                    isPlayerUiVisible.value = uiVisible
                } else {
                    isPlayerUiVisible.value = true
                }
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
                    .clickable { onClick() }
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
