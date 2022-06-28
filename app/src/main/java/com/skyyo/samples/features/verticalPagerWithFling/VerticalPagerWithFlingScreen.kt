package com.skyyo.samples.features.verticalPagerWithFling

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.skyyo.samples.features.exoPlayer.common.VideoItem
import com.skyyo.samples.features.verticalPagerWithFling.composables.AutoPlayVideoCard
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.SnapOffsets
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val MAX_ITEM_FLING = 1

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun VideoPagerWithFlingScreen(viewModel: VerticalPagerWithFlingViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val videos by viewModel.videos.observeAsState(listOf())
    val playingVideoItem = remember { mutableStateOf(videos.firstOrNull()) }
    val playingVideoIndex = remember { mutableStateOf<Int?>(null) }
    val videoPlaybackStateChangedListener = remember {
        object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_ENDED) {
                    playingVideoIndex.value?.let { index ->
                        Log.d("vitalik", index.toString())
                        coroutineScope.launch { listState.animateScrollToItem(index + 1) }
                    }
                }
            }
        }
    }
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().also {
            it.addListener(videoPlaybackStateChangedListener)
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
                targetIndex.coerceIn(startIndex - MAX_ITEM_FLING, startIndex + MAX_ITEM_FLING)
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
