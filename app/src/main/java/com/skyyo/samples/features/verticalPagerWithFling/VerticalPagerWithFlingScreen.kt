package com.skyyo.samples.features.verticalPagerWithFling

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.SurfaceView
import android.view.TextureView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.skyyo.samples.common.composables.CircularProgressIndicatorRow
import com.skyyo.samples.features.exoPlayer.common.VideoItem
import com.skyyo.samples.features.verticalPagerWithFling.composables.AutoPlayVideoCard
import kotlinx.coroutines.*
import java.io.File

@OptIn(ExperimentalPagerApi::class)
@Composable
fun VideoPagerWithFlingScreen(viewModel: VerticalPagerWithFlingViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val videos by viewModel.videos.observeAsState(listOf())

    CacheThumbnails(videos = videos, backgroundColor = Color.White) {
        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState(pageCount = videos.size)
        var playingVideoItem by remember(videos) { mutableStateOf(videos.firstOrNull()) }
        var isFirstFrameRendered by remember { mutableStateOf(false) }
        val exoPlayer = remember(videos) {
            ExoPlayer.Builder(context).build().apply {
                playWhenReady = true
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            val playingVideoIndex = videos.indexOfFirst { it == playingVideoItem }
                            if (playingVideoIndex != -1) {
                                val nextIndex = playingVideoIndex + 1
                                if (nextIndex < videos.size) {
                                    coroutineScope.launch { pagerState.animateScrollToPage(playingVideoIndex + 1) }
                                }
                            }
                        }
                    }

                    override fun onRenderedFirstFrame() {
                        isFirstFrameRendered = true
                    }
                })
            }
        }

        LaunchedEffect(videos) {
            if (videos.isEmpty()) return@LaunchedEffect
            snapshotFlow {
                pagerState.currentPage
            }.collect { itemIndex ->
                isFirstFrameRendered = false
                playingVideoItem = videos[itemIndex]
                exoPlayer.setMediaItem(MediaItem.fromUri(playingVideoItem!!.mediaUrl))
                exoPlayer.prepare()
            }
        }

        SideEffect {
            // is null only upon entering the screen
            if (playingVideoItem == null) exoPlayer.pause()
        }

        DisposableEffect(exoPlayer) {
            val lifecycleObserver = LifecycleEventObserver { _, event ->
                if (playingVideoItem != null) {
                    when (event) {
                        Lifecycle.Event.ON_START -> exoPlayer.play()
                        Lifecycle.Event.ON_STOP -> exoPlayer.pause()
                    }
                }
            }

            lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
                exoPlayer.release()
            }
        }

        VerticalPager(state = pagerState) { page ->
            val video = videos[page]
            AutoPlayVideoCard(
                videoItem = video,
                exoPlayer = exoPlayer,
                isPlaying = video.id == playingVideoItem?.id,
                isFirstFrameRendered = isFirstFrameRendered && video.id == playingVideoItem?.id
            )
        }
    }
}

@Composable
fun CacheThumbnails(videos: List<VideoItem>, backgroundColor: Color, content: @Composable () -> Unit) {
    if (videos.isEmpty()) {
        content()
        return
    }
    var isThumbnailsCachingCompleted by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val playerView = remember {
        PlayerView(context).apply {
            setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }
    }
    var indexToCache by remember(videos) { mutableStateOf(0) }
    val exoPlayer = remember(videos) {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
            volume = 0f
            addListener(object : Player.Listener {
                override fun onRenderedFirstFrame() {
                    super.onRenderedFirstFrame()
                    pause()
                    coroutineScope.launch(Dispatchers.IO) {
                        val videoView = playerView.videoSurfaceView!!
                        val bitmapWidth = videoView.width
                        val bitmapHeight = videoView.height

                        val isBitmapRendered: Boolean
                        var bitmapToCache = when(videoView) {
                            is TextureView -> {
                                isBitmapRendered = true
                                withContext(Dispatchers.Main) { videoView.getBitmap(bitmapWidth, bitmapHeight)!! }
                            }
                            else -> {
                                isBitmapRendered = false
                                Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.RGB_565)
                            }
                        }

                        if (!isBitmapRendered) {
                            val surfaceView = videoView as? SurfaceView
                            if (surfaceView != null) {
                                try {
                                    bitmapToCache = copyPixels(surfaceView, bitmapToCache)
                                } catch (e: CopyFailedException) {
                                    Log.e("Oops", e.pixelCopyErrorCode.toString())
                                }
                            }
                        }

                        cacheThumbnail(context, bitmapToCache, videos[indexToCache])

                        withContext(Dispatchers.Main) {
                            when (indexToCache) {
                                videos.size - 1 -> isThumbnailsCachingCompleted = true
                                else -> indexToCache++
                            }
                        }
                    }
                }
            })
        }
    }

    DisposableEffect(exoPlayer) {
        playerView.player = exoPlayer
        onDispose { playerView.player = null }
    }

    LaunchedEffect(indexToCache) {
        exoPlayer.setMediaItem(MediaItem.fromUri(videos[indexToCache].mediaUrl))
        exoPlayer.prepare()
    }

    when (isThumbnailsCachingCompleted) {
        true -> content()
        false -> {
            Box {
                AndroidView(
                    factory = { playerView },
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicatorRow()
                }
            }
        }
    }
}

private class CopyFailedException(val pixelCopyErrorCode: Int): Exception()

suspend fun copyPixels(surfaceView: SurfaceView, bitmap: Bitmap) = suspendCancellableCoroutine<Bitmap> { continuation ->
    PixelCopy.request(surfaceView, bitmap, {
        when(it) {
            PixelCopy.SUCCESS -> continuation.resumeWith(Result.success(bitmap))
            else -> continuation.resumeWith(Result.failure(CopyFailedException(it)))
        }
    }, Handler(Looper.getMainLooper()))
}

private fun cacheThumbnail(context: Context, bitmap: Bitmap, videoItem: VideoItem) {
    val thumbnailDir = File(context.cacheDir, "thumbnails")
    thumbnailDir.mkdirs()
    val outputFile = File(thumbnailDir, "${videoItem.id}.tmb")
    val outputStream = outputFile.outputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, outputStream)
    outputStream.flush()
    outputStream.close()
    videoItem.thumbnailFilePath = outputFile.absolutePath
}

private const val COMPRESS_QUALITY = 100
