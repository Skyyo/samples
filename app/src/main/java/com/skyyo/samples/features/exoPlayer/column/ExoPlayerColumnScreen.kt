package com.skyyo.samples.features.exoPlayer.column

import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.skyyo.samples.R
import com.skyyo.samples.features.exoPlayer.VideoItem
import com.skyyo.samples.theme.Shapes
import com.skyyo.samples.utils.OnClick


//TODO optimize by using thumbnails instead of PlayerViews everywhere.
//TODO add seekTo & storing of last known position
//TODO there is a bug because next videos has last frame of last played video when starting
@Composable
fun ExoPlayerColumnScreen(viewModel: ExoPlayerColumnViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val exoPlayer = remember { SimpleExoPlayer.Builder(context).build() }
    val listState = rememberLazyListState()
    val videos by viewModel.videos.observeAsState(listOf())
    val playingVideoItem by viewModel.currentlyPlayingItem.observeAsState()

    val isCurrentItemVisible by derivedStateOf {
        isCurrentItemVisible(
            listState,
            playingVideoItem,
            videos
        )
    }

    LaunchedEffect(isCurrentItemVisible) {
        if (!isCurrentItemVisible && playingVideoItem != null) {
            viewModel.onPlayVideoClick(playingVideoItem)
            exoPlayer.pause()
        }
    }

    DisposableEffect(exoPlayer) {
        val observer = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                if (playingVideoItem != null) exoPlayer.play()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onStop() {
                if (playingVideoItem != null) exoPlayer.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    LaunchedEffect(playingVideoItem) {
        if (playingVideoItem == null) {
            exoPlayer.stop()
        } else {
            exoPlayer.playWhenReady = true
            exoPlayer.setMediaItem(MediaItem.fromUri(playingVideoItem!!.mediaUrl))
            exoPlayer.prepare()
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = true,
            applyBottom = true,
            additionalStart = 16.dp,
            additionalEnd = 16.dp,
            additionalBottom = 8.dp
        )
    ) {
        items(videos, { video -> video.id }) { video ->
            Spacer(modifier = Modifier.height(16.dp))
            VideoCard(
                videoItem = video,
                exoPlayer = exoPlayer,
                isPlaying = video.id == playingVideoItem?.id,
                onClick = { viewModel.onPlayVideoClick(video) }
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun VideoCard(
    modifier: Modifier = Modifier,
    videoItem: VideoItem,
    isPlaying: Boolean,
    exoPlayer: SimpleExoPlayer,
    onClick: OnClick
) {
    val context = LocalContext.current
    val isPlayerUiVisible = remember { mutableStateOf(false) }
    val isPlayIconShown = remember(isPlaying, isPlayerUiVisible.value) {
        mutableStateOf(if (isPlayerUiVisible.value) true else !isPlaying)
    }
    val exoPlayerPreview = remember {
        val layout = LayoutInflater.from(context).inflate(R.layout.video_player, null, false)
        val playerView = layout.findViewById(R.id.playerView) as PlayerView
        playerView.apply {
            setControllerVisibilityListener {
                if (isPlayerUiVisible.value) {
                    isPlayerUiVisible.value = it == View.VISIBLE
                } else {
                    isPlayerUiVisible.value = true
                }
            }
        }
    }
    val playStatusIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play

    LaunchedEffect(isPlaying) {
        exoPlayerPreview.player = if (isPlaying) exoPlayer else null
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.DarkGray, Shapes.medium)
            .clip(Shapes.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Video #${videoItem.id}")
        Box {
            AndroidView({ exoPlayerPreview }, Modifier.height(256.dp))
            if (isPlayIconShown.value) {
                Icon(
                    painter = painterResource(playStatusIcon),
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(72.dp)
                        .clickable {
                            isPlayIconShown.value = false
                            onClick()
                        })
            }
        }
    }

}

fun isCurrentItemVisible(
    listState: LazyListState,
    currentlyPlayedItem: VideoItem?,
    videos: List<VideoItem>
): Boolean {
    return if (currentlyPlayedItem == null) {
        false
    } else {
        val layoutInfo = listState.layoutInfo
        val visibleItems = layoutInfo.visibleItemsInfo.map { videos[it.index] }
        visibleItems.contains(currentlyPlayedItem)
    }
}

