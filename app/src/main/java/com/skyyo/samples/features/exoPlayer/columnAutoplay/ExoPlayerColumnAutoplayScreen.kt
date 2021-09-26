package com.skyyo.samples.features.exoPlayer.columnAutoplay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.skyyo.samples.features.exoPlayer.VideoItem
import com.skyyo.samples.theme.Shapes
import java.lang.Math.abs


//TODO optimize by using thumbnails instead of PlayerViews everywhere.
//TODO add seekTo & storing of last known position
//TODO customize UI because player looks bad atm
//TODO add playback for first & last items by adjusting findPlayingItemId using listState
//TODO there is a bug because next videos has last frame of last played video when starting
@Composable
fun ExoPlayerColumnAutoplayScreen(viewModel: ExoPlayerColumnAutoplayViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val listState = rememberLazyListState()
    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
        }
    }
    val videos by viewModel.videos.observeAsState(listOf())
    // not sure we need derivedState for videos here
    val currentlyPlayingId by derivedStateOf { findPlayingItemId(listState, videos) }

    //optimise by logging recompositions
    DisposableEffect(exoPlayer) {
        val observer = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                if (currentlyPlayingId != -1) exoPlayer.play()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                if (currentlyPlayingId != -1) exoPlayer.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    LaunchedEffect(currentlyPlayingId) {
        if (currentlyPlayingId == -1) {
            exoPlayer.stop()
        } else {
            val playingItem = videos.find { it.id == currentlyPlayingId }
            exoPlayer.setMediaItem(MediaItem.fromUri(playingItem!!.mediaUrl))
            exoPlayer.prepare()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
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
                isPlaying = video.id == currentlyPlayingId,
            )
        }
    }
}

@Composable
private fun VideoCard(
    modifier: Modifier = Modifier,
    videoItem: VideoItem,
    isPlaying: Boolean,
    exoPlayer: SimpleExoPlayer,
) {
    val context = LocalContext.current
    val exoPlayerPreview = remember {
        PlayerView(context).apply {
            //TODO add listeners for play/pause etc
        }
    }

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
        AndroidView({ exoPlayerPreview }, Modifier.height(256.dp))
    }
}


fun findPlayingItemId(
    listState: LazyListState,
    items: List<VideoItem>
): Int {
    val layoutInfo = listState.layoutInfo
    val visibleItems = layoutInfo.visibleItemsInfo.map { items[it.index] }
    return when (visibleItems.size) {
        1 -> visibleItems.first().id
        else -> {
            val midPoint = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            val itemsFromCenter =
                layoutInfo.visibleItemsInfo.sortedBy { abs((it.offset + it.size / 2) - midPoint) }
            return itemsFromCenter.map { items[it.index] }.firstOrNull()?.id ?: -1
        }
    }
}