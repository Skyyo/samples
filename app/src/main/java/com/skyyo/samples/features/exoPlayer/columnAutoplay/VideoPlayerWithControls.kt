package com.skyyo.samples.features.exoPlayer.columnAutoplay

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.skyyo.samples.R

@Composable
fun VideoPlayerWithControls(exoPlayer: SimpleExoPlayer) {
    val context = LocalContext.current
    val playerView = remember {
        val layout = LayoutInflater.from(context).inflate(R.layout.video_player_auto, null)
        layout.findViewById<ImageButton>(R.id.exo_pause).setOnClickListener { exoPlayer.pause() }
        layout.findViewById<ImageButton>(R.id.exo_play).setOnClickListener { exoPlayer.play() }
        val playerView = (layout.findViewById(R.id.playerView) as PlayerView).apply {
            player = exoPlayer
        }
        layout.id = View.generateViewId()
        playerView
//        layout
    }

//    DisposableEffect(Unit) {
//        onDispose {
//            log("disposing view")
//            playerView.player?.pause()
////            playerView.player = null
////            playerView.removeAllViewsInLayout()
////            playerView = null
//        }
//    }

    AndroidView(
        { playerView },
        Modifier
            .height(256.dp)
            .background(Color.Black)
    )
}