package com.skyyo.samples.features.exoPlayer.common

data class VideoItem(
    val id: Int,
    val mediaUrl: String,
    val thumbnail: String,
    var lastPlayedPosition: Long = 0
)