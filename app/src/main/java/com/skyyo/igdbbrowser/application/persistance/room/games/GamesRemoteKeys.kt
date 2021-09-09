package com.skyyo.igdbbrowser.application.persistance.room.games

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games_remote_keys")
data class GamesRemoteKeys(
    @PrimaryKey
    val gameId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)