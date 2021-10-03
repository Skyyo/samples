package com.skyyo.samples.features.exoPlayer.columnIndexed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.skyyo.samples.R
import com.skyyo.samples.extensions.log
import com.skyyo.samples.features.exoPlayer.VideoItemImmutable
import com.skyyo.samples.features.exoPlayer.composables.StaticVideoThumbnail
import com.skyyo.samples.theme.Shapes
import com.skyyo.samples.utils.OnClick


@Composable
fun ExoPlayerColumnIndexedScreen(viewModel: ExoPlayerColumnIndexedViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val exoPlayer = remember { SimpleExoPlayer.Builder(context).build() }
    val listState = rememberLazyListState()
    val videos by viewModel.videos.observeAsState(listOf())
    val playingItemIndex by viewModel.currentlyPlayingIndex.observeAsState()
    //TODO using derivedStateOf+isCurrentItemVisible causes leaks. Using Dispose instead, to stop
    // playback when item is out of screen bounds works properly with derivedStateOf, so that
    // we don't recompose the ExoPlayerColumnIndexedScreen infinitelym but it's not a proper approach
    // to stop playback
//    val isCurrentItemVisible by derivedStateOf {
//        isCurrentItemVisible(listState, playingItemIndex, videos)
//    }
    val isCurrentItemVisible = isCurrentItemVisible(listState, playingItemIndex, videos)
    LaunchedEffect(isCurrentItemVisible) {
        log("LaunchedEffect(isCurrentItemVisible)")
        if (!isCurrentItemVisible && playingItemIndex != null) {
            viewModel.onPlayVideoClick(exoPlayer.currentPosition, playingItemIndex!!)
            exoPlayer.pause()
        }
    }

    DisposableEffect(exoPlayer) {
        log("DisposableEffect(exoPlayer)")
        val observer = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                if (playingItemIndex != null) exoPlayer.play()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onStop() {
                if (playingItemIndex != null) exoPlayer.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    //this can be replaced with one shot events
    LaunchedEffect(playingItemIndex) {
        log("LaunchedEffect(playingItemIndex)")
        if (playingItemIndex == null) {
            if (exoPlayer.isPlaying) exoPlayer.pause()
        } else {
            exoPlayer.playWhenReady = true
            val video = videos[playingItemIndex!!]
            exoPlayer.setMediaItem(MediaItem.fromUri(video.mediaUrl), video.lastPlayedPosition)
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
        itemsIndexed(videos, { _, video -> video.id }) { index, video ->
            Spacer(modifier = Modifier.height(16.dp))
            VideoCard(
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

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun VideoCard(
    modifier: Modifier = Modifier,
    videoItem: VideoItemImmutable,
    isPlaying: Boolean,
    exoPlayer: SimpleExoPlayer,
    onClick: OnClick
) {
    val isPlayerUiVisible = remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.DarkGray, Shapes.medium)
            .clip(Shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        if (isPlaying) {
            VideoPlayerIndexed(exoPlayer) { uiVisible ->
                if (isPlayerUiVisible.value) {
                    isPlayerUiVisible.value = uiVisible
                } else {
                    isPlayerUiVisible.value = true
                }
            }
        } else {
            StaticVideoThumbnail(videoItem.thumbnail)
        }
        if (if (isPlayerUiVisible.value) true else !isPlaying) {
            Icon(
                painter = painterResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(72.dp)
                    .clickable { onClick() })
        }
        Text(
            text = "${videoItem.id}",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        )
    }
}

fun isCurrentItemVisible(
    listState: LazyListState,
    currentlyPlayedIndex: Int?,
    videos: List<VideoItemImmutable>
): Boolean {
//    log("isCurrentItemVisible")
    return if (currentlyPlayedIndex == null) {
        false
    } else {
        val layoutInfo = listState.layoutInfo
        val visibleItems = layoutInfo.visibleItemsInfo.map { videos[it.index] }
        visibleItems.contains(videos[currentlyPlayedIndex])
    }
}

