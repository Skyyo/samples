package com.skyyo.samples.features.exoPlayer

import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession

class SampleMediaSessionService: MediaLibraryService() {
    private lateinit var mediaLibrarySession: MediaLibrarySession
    private lateinit var player: ExoPlayer

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaLibrarySession

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
            .build()

        mediaLibrarySession = MediaLibrarySession.Builder(this, player, object: MediaLibrarySession.MediaLibrarySessionCallback {})
            .setMediaItemFiller(MediaItemFiller())
            .build()
    }

    class MediaItemFiller : MediaSession.MediaItemFiller {
        override fun fillInLocalConfiguration(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItem: MediaItem
        ): MediaItem {
            // Return the media item that will be played
            return MediaItem.Builder()
                // Use the metadata values to fill our media item
                .setUri(mediaItem.mediaMetadata.mediaUri)
                .setMediaMetadata(mediaItem.mediaMetadata)
                .build()
        }
    }

    override fun onDestroy() {
        mediaLibrarySession.release()
        player.release()
        super.onDestroy()
    }
}