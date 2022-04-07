package com.skyyo.samples.features.videopager

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.skyyo.samples.features.videopager.composables.VideoPager
import com.skyyo.samples.features.videopager.model.PagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun VideoPagerScreen(viewModel: VideoPagerViewModel = hiltViewModel()) {

    val videoUrls by viewModel.videos.collectAsState()

    ShortViewCompose(videoItemsUrl = videoUrls, clickItemPosition = 0)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ShortViewCompose(
    videoItemsUrl: List<String>,
    clickItemPosition: Int = 0,
) {
    val pagerState: PagerState = remember {
        PagerState(clickItemPosition, 0, videoItemsUrl.size - 1)
    }
    val isPauseIconVisible = remember { mutableStateOf(false) }
    VideoPager(
        state = pagerState,
        orientation = Orientation.Vertical,
        offscreenLimit = 1
    ) {
        isPauseIconVisible.value = false
        VideoPlayer(
            videoUrl = videoItemsUrl[page],
            pagerState = pagerState,
            page = page,
            isPauseIconVisibleState = isPauseIconVisible
        )
    }
}

@Composable
fun VideoPlayer(
    videoUrl: String,
    pagerState: PagerState,
    page: Int,
    isPauseIconVisibleState: MutableState<Boolean>,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(
                context,
                DefaultHttpDataSource.Factory().setUserAgent(context.packageName)
            )
            val mediaItem = MediaItem.Builder().setUri(Uri.parse(videoUrl)).build()
            val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)
            setMediaSource(source)
            prepare()
        }
    }
    with(exoPlayer) {
        when (page) {
            pagerState.currentPage -> {
                playWhenReady = true
                play()
            }
            else -> pause()
        }
        videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
        repeatMode = Player.REPEAT_MODE_ONE
    }

    DisposableEffect(
        Box(modifier = Modifier.fillMaxSize()){
            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        hideController()
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        player = exoPlayer
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(indication = null, interactionSource = MutableInteractionSource()) {
                        isPauseIconVisibleState.value = true
                        scope.launch {
                            delay(500)
                            if (exoPlayer.isPlaying) {
                                exoPlayer.pause()
                            } else {
                                isPauseIconVisibleState.value = false
                                exoPlayer.play()
                            }
                        }
                    }
            )
            if (isPauseIconVisibleState.value) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center).size(80.dp)
                )
            }
        }
    ) {
        onDispose { exoPlayer.release() }
    }
}