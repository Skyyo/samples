package com.skyyo.samples.features.exoPlayer.columnIndexed

import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.Image
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.decode.VideoFrameDecoder
import coil.fetch.VideoFrameFileFetcher
import coil.fetch.VideoFrameUriFetcher
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import coil.request.videoFrameOption
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.skyyo.samples.R
import com.skyyo.samples.theme.Shapes
import com.skyyo.samples.utils.OnClick


//TODO optimize by using thumbnails instead of PlayerViews everywhere.
//TODO there is a bug because next videos has last frame of last played video when starting
@Composable
fun ExoPlayerColumnIndexedScreen(viewModel: ExoPlayerColumnIndexedViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val exoPlayer = remember { SimpleExoPlayer.Builder(context).build() }
    val listState = rememberLazyListState()
    val videos by viewModel.videos.observeAsState(listOf())
    val playingItemIndex by viewModel.currentlyPlayingIndex.observeAsState()
    val isCurrentItemVisible by derivedStateOf {
        isCurrentItemVisible(
            listState,
            playingItemIndex,
            videos
        )
    }
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .componentRegistry {
                add(VideoFrameFileFetcher(context))
                add(VideoFrameUriFetcher(context))
                add(VideoFrameDecoder(context))
            }
            .build()
    }

    LaunchedEffect(isCurrentItemVisible) {
        if (!isCurrentItemVisible && playingItemIndex != null) {
            viewModel.onPlayVideoClick(exoPlayer.currentPosition, playingItemIndex!!)
            exoPlayer.pause()
        }
    }

    DisposableEffect(exoPlayer) {
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
            exoPlayer.release()
        }
    }

    //this can be replaced with one shot events
    LaunchedEffect(playingItemIndex) {
        if (playingItemIndex == null) {
            exoPlayer.stop()
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
                imageLoader = imageLoader,
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
    imageLoader: ImageLoader,
    videoItem: VideoItemImmutable,
    isPlaying: Boolean,
    exoPlayer: SimpleExoPlayer,
    onClick: OnClick
) {
    val context = LocalContext.current
    val isPlayerUiVisible = remember { mutableStateOf(false) }
    val isPlayIconShown = remember(isPlaying, isPlayerUiVisible.value) {
        mutableStateOf(if (isPlayerUiVisible.value) true else !isPlaying)
    }
    val exoPlayerPreview = remember {
        val layout = LayoutInflater.from(context).inflate(R.layout.video_player, null, false)
        val playerView = layout.findViewById(R.id.playerView) as PlayerView
        playerView.apply {
            setControllerVisibilityListener {
                if (isPlayerUiVisible.value) {
                    isPlayerUiVisible.value = it == View.VISIBLE
                } else {
                    isPlayerUiVisible.value = true
                }
            }
        }
    }
    val playStatusIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
    val videoThumbnail: MutableState<Drawable?> = remember {
        mutableStateOf(null)
    }
    val request by rememberUpdatedState(
        ImageRequest.Builder(context)
            .data(videoItem.mediaUrl)
            .size(256, 256)
            .videoFrameMillis(videoItem.lastPlayedPosition)
            .videoFrameOption(MediaMetadataRetriever.OPTION_PREVIOUS_SYNC)
            .build()
    )

    LaunchedEffect(isPlaying) {
        exoPlayerPreview.player = if (isPlaying) exoPlayer else null
        videoThumbnail.value = imageLoader.execute(request).drawable
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.DarkGray, Shapes.medium)
            .clip(Shapes.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Video #${videoItem.id}")
        Image(
            painter = rememberImagePainter(
                data = videoThumbnail.value
            ),
            contentDescription = null,
            modifier = Modifier.size(156.dp)
        )

        Box {
            AndroidView({ exoPlayerPreview }, Modifier.height(256.dp))
            if (isPlayIconShown.value) {
                Icon(
                    painter = painterResource(playStatusIcon),
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(72.dp)
                        .clickable {
                            isPlayIconShown.value = false
                            onClick()
                        })
            }
        }
    }
}

//TODO check
fun isCurrentItemVisible(
    listState: LazyListState,
    currentlyPlayedIndex: Int?,
    videos: List<VideoItemImmutable>
): Boolean {
    return if (currentlyPlayedIndex == null) {
        false
    } else {
        val layoutInfo = listState.layoutInfo
        val visibleItems = layoutInfo.visibleItemsInfo.map { videos[it.index] }
        layoutInfo.visibleItemsInfo
//        visibleItems.contains(currentlyPlayedItem)
        visibleItems.contains(videos[currentlyPlayedIndex])
    }
}

