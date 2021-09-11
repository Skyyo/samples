package com.skyyo.samples.application.models.remote

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "games_table")
@JsonClass(generateAdapter = true)
data class Game(
    @PrimaryKey
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
)