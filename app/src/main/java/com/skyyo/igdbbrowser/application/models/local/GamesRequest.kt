package com.skyyo.igdbbrowser.application.models.local

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GamesRequest(
    val limit: Int,
    val offset: Int,
    val fields: String
)