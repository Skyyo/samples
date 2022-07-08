package com.skyyo.samples.features.verticalPagerWithFling

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.skyyo.samples.extensions.log
import com.skyyo.samples.features.exoPlayer.common.VideoItem
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun VideoPagerWithFlingScreen(viewModel: VerticalPagerWithFlingViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    val videos by viewModel.videos.observeAsState(listOf())
    var playingVideoItem by remember { mutableStateOf(videos.firstOrNull()) }
    var playingVideoIndex by remember { mutableStateOf<Int?>(null) }
    var isFirstFrameRendered by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = videos.size)
    var bufferingTime by remember { mutableStateOf(System.currentTimeMillis()) }

    val playerEventsListener = remember {
        object : Player.Listener {

            override fun onRenderedFirstFrame() {
                isFirstFrameRendered = true
                super.onRenderedFirstFrame()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        log("state ready: ${System.currentTimeMillis() - bufferingTime}")
                    }
                    Player.STATE_BUFFERING -> {
                        bufferingTime = System.currentTimeMillis()
                        log("state buffering")
                    }
                    Player.STATE_ENDED -> {
                        playingVideoIndex?.let { index ->
                            val nextIndex = index + 1
                            if (nextIndex < videos.size) {
                                coroutineScope.launch { pagerState.animateScrollToPage(nextIndex) }
                            }
                        }
                    }
                }
                super.onPlaybackStateChanged(playbackState)
            }
        }
    }
    val cacheModule = remember { CacheModule(context) }
    val exoPlayer = rememberedExoPlayer(context, cacheModule, playerEventsListener)

    LaunchedEffect(videos) {
        if (videos.isNullOrEmpty()) return@LaunchedEffect

        // TODO ensure coroutine errors won't cancel other downloads
        // TODO move to VM layer with injection

        // pre caching all videos
        launch {
            videos.map { videoItem ->
                async {
                    if (!isActive) return@async
                    val mediaItem = MediaItem.Builder()
                        .setUri(videoItem.mediaUrl)
                        .setMimeType(MimeTypes.APPLICATION_M3U8)
                        .build()
                    if (!cacheModule.isUriCached(videoItem.mediaUrl)) {
                        cacheModule.preCacheUri(mediaItem)
                    }
                }
            }.awaitAll()
        }

        snapshotFlow {
            pagerState.currentPage
        }.collect { itemIndex ->
            isFirstFrameRendered = false
            playingVideoIndex = itemIndex
            playingVideoItem = videos[itemIndex]
        }
    }

    LaunchedEffect(playingVideoItem) {
        val video = playingVideoItem
        if (video == null) {
            isFirstFrameRendered = false
            exoPlayer.pause()
        } else {
            val mediaItem = MediaItem.Builder()
                .setUri(video.mediaUrl)
                .setMimeType(MimeTypes.APPLICATION_M3U8)
                .build()
            val mediaSource = cacheModule.getHlsMediaSource(mediaItem)
            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()
            if (!exoPlayer.playWhenReady) exoPlayer.playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (playingVideoItem == null) return@LifecycleEventObserver
            when (event) {
                Lifecycle.Event.ON_START -> exoPlayer.play()
                Lifecycle.Event.ON_STOP -> exoPlayer.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            exoPlayer.release()
            cacheModule.releaseCache()
        }
        // TODO doesn't work, why?
//        observeLifecycleEvents(exoPlayer, playingVideoItem, lifecycleOwner, cacheModule)
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val video = videos[page]
        val isThumbnailVisible =
            video.id != playingVideoItem?.id || video.id == playingVideoItem?.id && !isFirstFrameRendered

        VerticalPagerWithFlingPage(
            videoItem = video,
            exoPlayer = exoPlayer,
            isThumbnailVisible = isThumbnailVisible,
            isPlaying = video.id == playingVideoItem?.id,
            onClick = {
                playingVideoItem = if (playingVideoItem == video) null else video
            }
        )
    }
}

@Composable
private fun rememberedExoPlayer(
    context: Context,
    cacheModule: CacheModule,
    listener: Player.Listener
): ExoPlayer {
    return remember {
        val trackSelector = DefaultTrackSelector(context).apply {
            setParameters(buildUponParameters().setMaxVideoSize(1080, 1920))
        }
        // TODO such settings might be an issue under certain conditions, needs testing
        val loadControl = DefaultLoadControl.Builder().apply {
            setBufferDurationsMs(
                0, // DEFAULT_MIN_BUFFER_MS = 50_000;
                0, // DEFAULT_MAX_BUFFER_MS = 50_000;
                0, // DEFAULT_BUFFER_FOR_PLAYBACK_MS = 2500
                0 // DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 5000;
            )
        }.build()
        ExoPlayer.Builder(context).apply {
            setTrackSelector(trackSelector)
            setLoadControl(loadControl)
            // TODO does it make sense to use cacheModule.cacheDataSourceFactory or cronetDataSourceFactory directly?
            setMediaSourceFactory(
                DefaultMediaSourceFactory(context)
                    .setDataSourceFactory(cacheModule.cacheDataSourceFactory)
            )
        }.build().also {
            it.playWhenReady = true
            it.addListener(listener)
        }
    }
}

private fun DisposableEffectScope.observeLifecycleEvents(
    exoPlayer: ExoPlayer,
    playingVideoItem: VideoItem?,
    lifecycleOwner: LifecycleOwner,
    cacheModule: CacheModule
): DisposableEffectResult {
    val lifecycleObserver = LifecycleEventObserver { _, event ->
        if (playingVideoItem == null) return@LifecycleEventObserver
        when (event) {
            Lifecycle.Event.ON_START -> exoPlayer.play()
            Lifecycle.Event.ON_STOP -> exoPlayer.pause()
        }
    }
    lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
    return onDispose {
        lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        exoPlayer.release()
        cacheModule.releaseCache()
    }
}
