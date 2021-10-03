package com.skyyo.samples.features.exoPlayer.columnIndexed

import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.skyyo.samples.R
import com.skyyo.samples.extensions.log
import com.skyyo.samples.utils.OnClick

@Composable
fun VideoPlayerIndexed(
    exoPlayer: SimpleExoPlayer,
//    onDisposed: OnClick,
    onControllerVisibilityChanged: (uiVisible: Boolean) -> Unit
) {
    log("VideoPlayerIndexed")
    val context = LocalContext.current
    val playerView = remember {
        val layout = LayoutInflater.from(context).inflate(R.layout.video_player, null, false)
        val playerView = layout.findViewById(R.id.playerView) as PlayerView
        playerView.apply {
            setControllerVisibilityListener { onControllerVisibilityChanged(it == View.VISIBLE) }
            player = exoPlayer
        }
    }

    //dispose approach doesn't work to pause player since it's not invoked instantly
    // when view is not visible to the user
//    DisposableEffect(Unit) {
//        onDispose { onDisposed() }
//    }

    AndroidView(
        { playerView },
        Modifier
            .height(256.dp)
            .background(Color.Black)
    )
}