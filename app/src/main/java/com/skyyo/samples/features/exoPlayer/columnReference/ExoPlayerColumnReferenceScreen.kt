package com.skyyo.samples.features.exoPlayer.columnReference

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.skyyo.samples.features.exoPlayer.common.VideoItem
import kotlinx.coroutines.flow.collect

@Composable
fun ExoPlayerColumnReferenceScreen(viewModel: ExoPlayerColumnReferenceViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val exoPlayer = remember { SimpleExoPlayer.Builder(context).build() }
    val listState = rememberLazyListState()
    val videos by viewModel.videos.observeAsState(listOf())
    val playingVideoItem by viewModel.currentlyPlayingItem.observeAsState()
    val isPlayingItemVisible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        snapshotFlow {
            listState.visibleAreaContainsItem(playingVideoItem, videos)
        }.collect { isVisible ->
            isPlayingItemVisible.value = isVisible
        }
    }

    LaunchedEffect(playingVideoItem) {
        if (playingVideoItem == null) {
            exoPlayer.pause()
        } else {
            exoPlayer.setMediaItem(
                MediaItem.fromUri(playingVideoItem!!.mediaUrl),
                playingVideoItem!!.lastPlayedPosition
            )
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    LaunchedEffect(isPlayingItemVisible.value) {
        if (!isPlayingItemVisible.value && playingVideoItem != null) {
            viewModel.onPlayVideoClick(exoPlayer.currentPosition, playingVideoItem)
        }
    }

    DisposableEffect(exoPlayer) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (playingVideoItem == null) return@LifecycleEventObserver
            when (event) {
                Lifecycle.Event.ON_START -> exoPlayer.play()
                Lifecycle.Event.ON_STOP -> exoPlayer.pause()
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            exoPlayer.release()
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
        items(videos, VideoItem::id) { video ->
            Spacer(modifier = Modifier.height(16.dp))
            VideoCardReference(
                videoItem = video,
                exoPlayer = exoPlayer,
                isPlaying = video.id == playingVideoItem?.id,
                onClick = {
                    viewModel.onPlayVideoClick(exoPlayer.currentPosition, video)
                }
            )
        }
    }
}

private fun LazyListState.visibleAreaContainsItem(
    currentlyPlayedVideoItem: VideoItem?,
    videos: List<VideoItem>
) = when {
    currentlyPlayedVideoItem == null -> false
    videos.isEmpty() -> false
    else -> {
        layoutInfo.visibleItemsInfo.map { videos[it.index] }.contains(currentlyPlayedVideoItem)
    }
}

