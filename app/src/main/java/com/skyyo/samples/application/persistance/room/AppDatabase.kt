package com.skyyo.samples.application.persistance.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skyyo.samples.application.models.remote.Game
import com.skyyo.samples.application.persistance.room.games.GamesDao
import com.skyyo.samples.application.persistance.room.games.GamesRemoteKeys
import com.skyyo.samples.application.persistance.room.games.GamesRemoteKeysDao

@Database(
    version = 2,
    entities = [
        Game::class,
        GamesRemoteKeys::class,
    ],
)

@TypeConverters(MoshiTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gamesDao(): GamesDao
    abstract fun gamesRemoteKeysDao(): GamesRemoteKeysDao

}
