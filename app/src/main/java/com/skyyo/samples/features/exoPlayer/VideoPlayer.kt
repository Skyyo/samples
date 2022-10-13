package com.skyyo.samples.features.exoPlayer

import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.skyyo.samples.R

@Composable
fun VideoPlayer(
    exoPlayer: ExoPlayer,
    onControllerVisibilityChanged: (uiVisible: Boolean) -> Unit
) {
    val context = LocalContext.current
    val playerView = remember {
        val layout = LayoutInflater.from(context).inflate(R.layout.video_player, null, false)
        val playerView = layout.findViewById(R.id.playerView) as PlayerView
        playerView.apply {
            setControllerVisibilityListener(
                PlayerView.ControllerVisibilityListener { visibility ->
                    onControllerVisibilityChanged(visibility == View.VISIBLE)
                }
            )

            player = exoPlayer
        }
    }

    // dispose approach doesn't work to pause player since it's not invoked as soon as
    // view is not visible to the user
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
