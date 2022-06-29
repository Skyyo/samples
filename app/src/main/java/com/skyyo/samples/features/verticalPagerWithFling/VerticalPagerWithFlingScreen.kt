package com.skyyo.samples.features.verticalPagerWithFling

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.skyyo.samples.features.verticalPagerWithFling.composables.AutoPlayVideoCard
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun VideoPagerWithFlingScreen(viewModel: VerticalPagerWithFlingViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val videos by viewModel.videos.observeAsState(listOf())
    val pagerState = rememberPagerState(pageCount = videos.size)

    val playingVideoItem = remember { mutableStateOf(videos.firstOrNull()) }
    val playingVideoIndex = remember { mutableStateOf<Int?>(null) }
    val videoPlaybackStateChangedListener = remember {
        object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_ENDED) {
                    playingVideoIndex.value?.let { index ->
                        val nextIndex = index + 1
                        if (nextIndex < videos.size) {
                            coroutineScope.launch { pagerState.animateScrollToPage(index + 1) }
                        }
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

    LaunchedEffect(videos) {
        if (videos.isEmpty()) return@LaunchedEffect
        snapshotFlow {
            pagerState.currentPage
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

    VerticalPager(state = pagerState) { page ->
        key(page) {
            val video = videos[page]
            AutoPlayVideoCard(
                videoItem = video,
                exoPlayer = exoPlayer,
                isPlaying = video.id == playingVideoItem.value?.id,
            )
        }
    }
}
