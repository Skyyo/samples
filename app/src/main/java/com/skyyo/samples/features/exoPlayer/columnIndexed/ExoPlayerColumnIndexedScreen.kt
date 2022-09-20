package com.skyyo.samples.features.exoPlayer.columnIndexed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.skyyo.samples.features.exoPlayer.common.VideoItemImmutable

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ExoPlayerColumnIndexedScreen(viewModel: ExoPlayerColumnIndexedViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val exoPlayer = remember(context) { ExoPlayer.Builder(context).build() }
    val listState = rememberLazyListState()

    val videos by viewModel.videos.collectAsStateWithLifecycle()
    val playingItemIndex by viewModel.currentlyPlayingIndex.collectAsStateWithLifecycle()
    var isCurrentItemVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        snapshotFlow {
            listState.visibleAreaContainsItem(playingItemIndex, videos)
        }.collect { isVisible ->
            isCurrentItemVisible = isVisible
        }
    }

    LaunchedEffect(playingItemIndex) {
        if (playingItemIndex == null) {
            exoPlayer.pause()
        } else {
            val video = videos[playingItemIndex!!]
            exoPlayer.setMediaItem(MediaItem.fromUri(video.mediaUrl), video.lastPlayedPosition)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    LaunchedEffect(isCurrentItemVisible) {
        if (!isCurrentItemVisible && playingItemIndex != null) {
            viewModel.onPlayVideoClick(exoPlayer.currentPosition, playingItemIndex!!)
        }
    }

    DisposableEffect(exoPlayer) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (playingItemIndex == null) return@LifecycleEventObserver
            when (event) {
                Lifecycle.Event.ON_START -> exoPlayer.play()
                Lifecycle.Event.ON_STOP -> exoPlayer.pause()
                else -> {}
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
        contentPadding = WindowInsets.systemBars
            .only(WindowInsetsSides.Vertical)
            .add(WindowInsets(left = 16.dp, right = 16.dp, bottom = 8.dp))
            .asPaddingValues()
    ) {
        itemsIndexed(videos, { _, video -> video.id }) { index, video ->
            Spacer(modifier = Modifier.height(16.dp))
            VideoCardIndexed(
                videoItem = video,
                exoPlayer = exoPlayer,
                isPlaying = index == playingItemIndex,
                onClick = {
                    viewModel.onPlayVideoClick(exoPlayer.currentPosition, index)
                }
            )
        }
    }
}

private fun LazyListState.visibleAreaContainsItem(
    currentlyPlayedIndex: Int?,
    videos: List<VideoItemImmutable>
) = when {
    currentlyPlayedIndex == null -> false
    videos.isEmpty() -> false
    else -> {
        layoutInfo.visibleItemsInfo.map { videos[it.index] }
            .contains(videos[currentlyPlayedIndex])
    }
}
