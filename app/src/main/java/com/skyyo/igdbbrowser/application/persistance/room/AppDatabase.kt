package com.skyyo.igdbbrowser.application.persistance.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skyyo.igdbbrowser.application.models.remote.Game

@Database(
    version = 1,
    entities = [
        Game::class,
    ],
)

@TypeConverters(MoshiTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gamesDao(): GamesDao

}
