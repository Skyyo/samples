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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.skyyo.samples.extensions.log
import com.skyyo.samples.features.exoPlayer.VideoItem
import com.skyyo.samples.features.exoPlayer.composables.StaticVideoThumbnail
import com.skyyo.samples.features.exoPlayer.composables.VideoPlayerWithControls
import com.skyyo.samples.theme.Shapes
import kotlin.math.abs

@Composable
fun ExoPlayerColumnAutoplayScreen(viewModel: ExoPlayerColumnAutoplayViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val listState = rememberLazyListState()
    val exoPlayer = remember { SimpleExoPlayer.Builder(context).build() }
    val videos by viewModel.videos.observeAsState(listOf())
    val playingVideoItem by derivedStateOf { findPlayingItem(listState, videos) }
//    val imageLoader = remember {
//        ImageLoader.Builder(context)
//            .componentRegistry {
//                add(VideoFrameFileFetcher(context))
//                add(VideoFrameUriFetcher(context))
//                add(VideoFrameDecoder(context))
//            }
//            .build()
//    }

    DisposableEffect(exoPlayer) {
        val observer = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                if (playingVideoItem != null) exoPlayer.play()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                if (playingVideoItem != null) exoPlayer.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    //TODO can be removed with doing this on item click/init if playerView is reused?
    LaunchedEffect(playingVideoItem) {
        if (playingVideoItem == null) {
            if (exoPlayer.isPlaying) exoPlayer.stop()
        } else {
            // move playWhenReady to exoPlayer initialization if you don't want to play next video
            // automatically
            exoPlayer.playWhenReady = true
            exoPlayer.setMediaItem(MediaItem.fromUri(playingVideoItem!!.mediaUrl))
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
        items(videos, VideoItem::id) { video ->
            Spacer(modifier = Modifier.height(16.dp))
            VideoCard(
//                imageLoader = imageLoader,
                videoItem = video,
                exoPlayer = exoPlayer,
                isPlaying = video.id == playingVideoItem?.id,
            )
        }
    }
}

@Composable
private fun VideoCard(
//    imageLoader: ImageLoader,
    videoItem: VideoItem,
    isPlaying: Boolean,
    exoPlayer: SimpleExoPlayer,
) {
    Box(
        modifier = Modifier
            .height(256.dp)
            .fillMaxWidth()
            .background(Color.DarkGray, Shapes.medium)
            .clip(Shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        if (isPlaying) {
            VideoPlayerWithControls(exoPlayer)
        } else {
            StaticVideoThumbnail(videoItem.thumbnail)
//            DynamicVideoThumbnail(imageLoader, videoItem.mediaUrl, videoItem.lastPlayedPosition)
        }
        Text(
            text = "${videoItem.id}",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        )
    }
}

fun findPlayingItem(
    listState: LazyListState,
    videos: List<VideoItem>
): VideoItem? {
    if (listState.layoutInfo.visibleItemsInfo.isNullOrEmpty()) return null

    val layoutInfo = listState.layoutInfo
    val visibleItems = layoutInfo.visibleItemsInfo

    val firstItemVisible =
        listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
    if (firstItemVisible) return videos.first()

    val lastItem = visibleItems.last()
    val lastItemVisible = lastItem.index == videos.size - 1
    if (lastItemVisible) {
        val itemSize = lastItem.size
        val itemOffset = lastItem.offset
        val totalOffset = layoutInfo.viewportEndOffset
        if (totalOffset - itemOffset >= itemSize) return videos.last()
    }

    val midPoint = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
    val centerItems = visibleItems.sortedBy { abs((it.offset + it.size / 2) - midPoint) }
    return centerItems.firstNotNullOf { videos[it.index] }
}