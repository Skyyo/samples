package com.skyyo.samples.features.exoPlayer.column

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.skyyo.samples.R
import com.skyyo.samples.features.exoPlayer.VideoItem
import com.skyyo.samples.features.exoPlayer.VideoPlayer
import com.skyyo.samples.features.exoPlayer.composables.StaticVideoThumbnail
import com.skyyo.samples.theme.Shapes
import com.skyyo.samples.utils.OnClick
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun ExoPlayerColumnScreen(viewModel: ExoPlayerColumnViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val exoPlayer = remember { SimpleExoPlayer.Builder(context).build() }
    val listState = rememberLazyListState()
    val videos by viewModel.videos.observeAsState(listOf())
    val playingVideoItem by viewModel.currentlyPlayingItem.observeAsState()
    val isPlayingItemVisible by listState.isCurrentItemVisible(playingVideoItem, videos)

    //can/should be provided by dagger if used in multiple places
//    val imageLoader = remember {
//        ImageLoader.Builder(context)
//            .componentRegistry {
//                add(VideoFrameFileFetcher(context))
//                add(VideoFrameUriFetcher(context))
//                add(VideoFrameDecoder(context))
//            }
//            .build()
//    }

    LaunchedEffect(isPlayingItemVisible) {
        if (!isPlayingItemVisible && playingVideoItem != null) {
            viewModel.onPlayVideoClick(exoPlayer.currentPosition, playingVideoItem)
            exoPlayer.pause()
        }
    }

    //this can be replaced with one shot events
    LaunchedEffect(playingVideoItem) {
        if (playingVideoItem == null) {
            if (exoPlayer.isPlaying) exoPlayer.pause()
        } else {
            exoPlayer.playWhenReady = true
            exoPlayer.setMediaItem(
                MediaItem.fromUri(playingVideoItem!!.mediaUrl),
                playingVideoItem!!.lastPlayedPosition
            )
            exoPlayer.prepare()
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
            exoPlayer.stop()
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
            VideoCard(
                videoItem = video,
                exoPlayer = exoPlayer,
//                imageLoader = imageLoader,
                isPlaying = video.id == playingVideoItem?.id,
                onClick = {
                    viewModel.onPlayVideoClick(exoPlayer.currentPosition, video)
                }
            )
        }
    }
}

@Composable
private fun VideoCard(
    modifier: Modifier = Modifier,
//    imageLoader: ImageLoader,
    videoItem: VideoItem,
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
            VideoPlayer(exoPlayer) { uiVisible ->
                if (isPlayerUiVisible.value) {
                    isPlayerUiVisible.value = uiVisible
                } else {
                    isPlayerUiVisible.value = true
                }
            }
        } else {
            StaticVideoThumbnail(videoItem.thumbnail)
//            DynamicVideoThumbnail(imageLoader, videoItem.mediaUrl, videoItem.lastPlayedPosition)
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

@Composable
fun LazyListState.isCurrentItemVisible(
    currentlyPlayedVideoItem: VideoItem?,
    videos: List<VideoItem>,
): State<Boolean> {
    val isVisible = remember {
        mutableStateOf(visibleAreaContainsPlayingItem(currentlyPlayedVideoItem, videos))
    }
    LaunchedEffect(this, currentlyPlayedVideoItem) {
        snapshotFlow {
            visibleAreaContainsPlayingItem(currentlyPlayedVideoItem, videos)
        }.distinctUntilChanged().collect {
            isVisible.value = visibleAreaContainsPlayingItem(currentlyPlayedVideoItem, videos)
        }
    }
    return isVisible
}

fun LazyListState.visibleAreaContainsPlayingItem(
    currentlyPlayedVideoItem: VideoItem?,
    videos: List<VideoItem>
) = when {
    currentlyPlayedVideoItem == null -> false
    videos.isEmpty() -> false
    else -> {
        layoutInfo.visibleItemsInfo.map { videos[it.index] }.contains(currentlyPlayedVideoItem)
    }
}

