package com.skyyo.samples.features.exoPlayer.columnReference

import android.annotation.SuppressLint
import android.content.ComponentName
import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.skyyo.samples.features.exoPlayer.SampleMediaSessionService
import com.skyyo.samples.features.exoPlayer.common.VideoItem
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@SuppressLint("UnsafeOptInUsageError")
@OptIn(UnstableApi::class)
@Composable
fun ExoPlayerColumnReferenceScreen(viewModel: ExoPlayerColumnReferenceViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isPlaying by remember { mutableStateOf(false) }
    val exoPlayer by remember {
        val mediaBrowserFuture = MediaBrowser.Builder(
            context,
            SessionToken(context, ComponentName(context, SampleMediaSessionService::class.java))
        ).buildAsync()

        val callback = object : Player.Listener {
            override fun onIsPlayingChanged(isPlayerPlaying: Boolean) {
                super.onIsPlayingChanged(isPlayerPlaying)
                isPlaying = isPlayerPlaying
            }
        }
        val state = mutableStateOf<Player?>(null)
        mediaBrowserFuture.addListener({
            state.value = mediaBrowserFuture.get().apply { addListener(callback) }
        }, Dispatchers.IO.asExecutor())

        state
    }

    val listState = rememberLazyListState()
    val videos by viewModel.videos.observeAsState(listOf())
    val playingVideoItem by viewModel.currentlyPlayingItem.observeAsState()

    LaunchedEffect(exoPlayer, playingVideoItem) {
        if (playingVideoItem == null) {
            exoPlayer?.pause()
        } else {

            // for some reason we need to set media item filler to create new media item
            // as copy of current media item, otherwise exoPlayer in MediaSessionService will crash
            exoPlayer?.apply {
                val mmd = MediaMetadata.Builder().setMediaUri(Uri.parse(playingVideoItem!!.mediaUrl)).build()

                setMediaItem(
                    MediaItem.Builder().setMediaMetadata(mmd).build(), playingVideoItem!!.lastPlayedPosition
                )
                prepare()
                playWhenReady = true
            }
        }
    }

    DisposableEffect(exoPlayer) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (playingVideoItem == null) return@LifecycleEventObserver
            when (event) {
                Lifecycle.Event.ON_START -> exoPlayer?.play()
                Lifecycle.Event.ON_STOP -> exoPlayer?.pause()
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer?.release() }
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
        when (val player = exoPlayer) {
            null -> {}
            else -> {
                items(videos, VideoItem::id) { video ->
                    Spacer(modifier = Modifier.height(16.dp))
                    VideoCardReference(
                        videoItem = video,
                        exoPlayer = player,
                        isPlaying = video.id == playingVideoItem?.id && isPlaying,
                        onClick = {
                            viewModel.onPlayVideoClick(player.currentPosition, video)
                        }
                    )
                }
            }
        }
    }
}

