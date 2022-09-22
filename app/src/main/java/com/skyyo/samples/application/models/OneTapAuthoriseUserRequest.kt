package com.skyyo.samples.application.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class OneTapAuthoriseUserRequest(val token: String)
