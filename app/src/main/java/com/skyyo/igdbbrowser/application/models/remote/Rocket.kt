package com.skyyo.igdbbrowser.application.models.remote

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "rockets_table")
@JsonClass(generateAdapter = true)
data class Rocket(
    @PrimaryKey @Json(name = "id")
    val rocketId: String,
    @Json(name = "rocket_name")
    val rocketName: String
)
