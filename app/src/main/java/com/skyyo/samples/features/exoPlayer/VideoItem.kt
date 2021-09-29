package com.skyyo.samples.features.exoPlayer

data class VideoItem(
    val id: Int,
    val mediaUrl: String,
    var lastPlayedPosition: Long = 0
)