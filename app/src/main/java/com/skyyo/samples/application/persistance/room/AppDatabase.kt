package com.skyyo.samples.application.persistance.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skyyo.samples.application.models.remote.Cat
import com.skyyo.samples.application.persistance.room.cats.CatsDao
import com.skyyo.samples.application.persistance.room.cats.CatsRemoteKeys
import com.skyyo.samples.application.persistance.room.cats.CatsRemoteKeysDao

@Database(
    version = 1,
    entities = [
        Cat::class,
        CatsRemoteKeys::class,
    ],
)

@TypeConverters(MoshiTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catsDao(): CatsDao
    abstract fun catsRemoteKeysDao(): CatsRemoteKeysDao

}
