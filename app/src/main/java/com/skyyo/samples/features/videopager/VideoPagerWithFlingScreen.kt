package com.skyyo.samples.features.videopager

import android.net.Uri
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.skyyo.samples.features.exoPlayer.common.VideoItem
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.SnapOffsets
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val MAX_ITEM_FLING = 1

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun VideoPagerWithFlingScreen(viewModel: VideoPagerWithFlingViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val listState = rememberLazyListState()
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    val videos by viewModel.videos.observeAsState(listOf())
    val playingVideoItem = remember { mutableStateOf(videos.firstOrNull()) }
    val playingVideoIndex = remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val flingListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            if (playbackState == Player.STATE_ENDED) {
                coroutineScope.launch {
                    playingVideoIndex .value?.let { index ->
                        listState.animateScrollToItem(index)
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            listState.firstVisibleItemIndex
        }.collect { itemIndex ->
            playingVideoIndex.value = itemIndex
            playingVideoItem.value = videos[itemIndex]
        }
    }

    LaunchedEffect(playingVideoItem.value) {
        // is null only upon entering the screen
        if (playingVideoItem.value == null) {
            exoPlayer.pause()
        } else {
            exoPlayer.setMediaItem(MediaItem.fromUri(playingVideoItem.value!!.mediaUrl))
            exoPlayer.prepare()
            exoPlayer.addListener(flingListener)
            exoPlayer.playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (playingVideoItem.value == null) return@LifecycleEventObserver
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
        modifier = Modifier.fillMaxSize(),
        state = listState,
        flingBehavior = rememberSnapperFlingBehavior(
            lazyListState = listState,
            snapOffsetForItem = SnapOffsets.Center,
            snapIndex = { _, startIndex, targetIndex ->
                targetIndex.coerceIn(startIndex - MAX_ITEM_FLING, startIndex + MAX_ITEM_FLING )
            }
        )
    ) {
        items(videos, VideoItem::id) { video ->
            AutoPlayVideoCard(
                modifier = Modifier.fillParentMaxSize(),
                videoItem = video,
                exoPlayer = exoPlayer,
                isPlaying = video.id == playingVideoItem.value?.id,
            )
        }
    }
}