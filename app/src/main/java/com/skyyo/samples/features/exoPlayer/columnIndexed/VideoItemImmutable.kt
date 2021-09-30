package com.skyyo.samples.features.exoPlayer.columnIndexed

import androidx.compose.runtime.Immutable


@Immutable
data class VideoItemImmutable(
    val id: Int,
    val mediaUrl: String,
    val lastPlayedPosition: Long = 0
)