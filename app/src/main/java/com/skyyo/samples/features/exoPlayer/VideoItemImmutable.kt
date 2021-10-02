package com.skyyo.samples.features.exoPlayer

import androidx.compose.runtime.Immutable


@Immutable
data class VideoItemImmutable(
    val id: Int,
    val mediaUrl: String,
    val thumbnail: String,
    val lastPlayedPosition: Long = 0
)