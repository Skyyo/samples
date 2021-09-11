package com.skyyo.samples.application.models.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class TwitchSignInResponse(
    @Json(name = "access_token")
    val accessToken: String,
)