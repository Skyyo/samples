package com.skyyo.samples.features.exoPlayer.columnAutoplay

import android.view.LayoutInflater
import android.widget.ImageButton
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
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.skyyo.samples.R
import com.skyyo.samples.features.exoPlayer.VideoItem
import com.skyyo.samples.theme.Shapes
import kotlin.math.abs


//TODO optimize by using thumbnails instead of PlayerViews everywhere.
//TODO there is a bug because next videos has last frame of last played video when starting

@Composable
fun ExoPlayerColumnAutoplayScreen(viewModel: ExoPlayerColumnAutoplayViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val listState = rememberLazyListState()
    val exoPlayer = remember { SimpleExoPlayer.Builder(context).build() }
    val videos by viewModel.videos.observeAsState(listOf())
    val playingVideoItem by derivedStateOf { findPlayingItemId(listState, videos) }

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

    LaunchedEffect(playingVideoItem) {
        if (playingVideoItem == null) {
            exoPlayer.stop()
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
        items(videos, { video -> video.id }) { video ->
            Spacer(modifier = Modifier.height(16.dp))
            VideoCard(
                videoItem = video,
                exoPlayer = exoPlayer,
                isPlaying = video.id == playingVideoItem?.id,
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
        val layout = LayoutInflater.from(context).inflate(R.layout.video_player_auto, null, false)
        layout.findViewById<ImageButton>(R.id.exo_pause).setOnClickListener { exoPlayer.pause() }
        layout.findViewById<ImageButton>(R.id.exo_play).setOnClickListener { exoPlayer.play() }
        layout.findViewById(R.id.playerView) as PlayerView
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
    videos: List<VideoItem>
): VideoItem? {
    if (listState.layoutInfo.visibleItemsInfo.isNullOrEmpty()) return null

    val layoutInfo = listState.layoutInfo
    val visibleItems = layoutInfo.visibleItemsInfo

    val isFirstItemVisible = listState.firstVisibleItemIndex == 0
    val isFirstItemPlayerVisible = listState.firstVisibleItemScrollOffset <= 256.dp.value
    val totalOffset = layoutInfo.viewportEndOffset

    val isLastItemVisible = when (visibleItems.last().index) {
        videos.size - 1 -> true
        else -> false
    }
    val itemSize = visibleItems.last().size
    val itemOffset = visibleItems.last().offset

    return when {
        //last video should be played
        isLastItemVisible && totalOffset - itemOffset >= itemSize -> videos.last()
        //first video should be played
        isFirstItemVisible && isFirstItemPlayerVisible -> videos.first()
        visibleItems.size == 1 -> videos.first()
        else -> {
            val midPoint = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            val centerItems = visibleItems.sortedBy { abs((it.offset + it.size / 2) - midPoint) }
            return centerItems.firstNotNullOf { videos[it.index] }
        }
    }
}