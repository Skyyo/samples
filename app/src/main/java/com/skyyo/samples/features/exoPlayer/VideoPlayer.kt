package com.skyyo.samples.features.exoPlayer

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.skyyo.samples.R

@Composable
fun VideoPlayer(
    exoPlayer: SimpleExoPlayer,
    onControllerVisibilityChanged: (uiVisible: Boolean) -> Unit
) {
    val context = LocalContext.current
    val playerView = remember {
        val layout = LayoutInflater.from(context).inflate(R.layout.video_player, null, false)
        val playerView = layout.findViewById(R.id.playerView) as PlayerView
        playerView.apply {
            setControllerVisibilityListener { onControllerVisibilityChanged(it == View.VISIBLE) }
            player = exoPlayer
        }
    }
    AndroidView({ playerView }, Modifier.height(256.dp))
}

@Composable
fun VideoPlayerWithControls(
    exoPlayer: SimpleExoPlayer,
) {
    val context = LocalContext.current
    val playerView: PlayerView = remember {
        val layout = LayoutInflater.from(context).inflate(R.layout.video_player_auto, null, false)
        layout.findViewById<ImageButton>(R.id.exo_pause).setOnClickListener { exoPlayer.pause() }
        layout.findViewById<ImageButton>(R.id.exo_play).setOnClickListener { exoPlayer.play() }
        (layout.findViewById(R.id.playerView) as PlayerView).apply {
            player = exoPlayer
        }
    }
//    DisposableEffect(Unit) {
//        onDispose {
//            playerView.player = null
////            playerView = null
//        }
//    }

    AndroidView({ playerView }, Modifier.height(256.dp))
}