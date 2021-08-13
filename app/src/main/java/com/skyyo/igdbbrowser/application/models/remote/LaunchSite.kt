package com.skyyo.igdbbrowser.application.models.remote

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "launch_sites_table")
@JsonClass(generateAdapter = true)
data class LaunchSite(
    @PrimaryKey @Json(name = "site_id")
    val siteId: Int,
    @Json(name = "site_name")
    val siteName: String,
    @Json(name = "site_name_long")
    val siteNameLong: Rocket
)