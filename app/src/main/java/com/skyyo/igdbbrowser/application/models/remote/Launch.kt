package com.skyyo.igdbbrowser.application.models.remote

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class LaunchesWrapper(
    @Json(name = "docs")
    val launches: List<Launch>
)

@Entity(tableName = "launches_table")
@JsonClass(generateAdapter = true)
data class Launch(
    @PrimaryKey @Json(name = "flight_number")
    val flightNumber: Int,
    @Json(name = "name")
    val name: String,
//    @Json(name = "rocket")
//    val rocket: Rocket,
//    @Json(name = "launch_site")
//    val launchSite: LaunchSite? = null,
    @Json(name = "upcoming")
    val upcoming: Boolean,
    @Json(name = "date_utc")
    val launchDate: String
)