package com.skyyo.composespacex.application.persistance.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skyyo.composespacex.application.models.remote.Launch
import com.skyyo.composespacex.application.models.remote.LaunchSite
import com.skyyo.composespacex.application.models.remote.Rocket

@Database(
    version = 1,
    entities = [
        Launch::class,
        LaunchSite::class,
        Rocket::class,
    ],
)

@TypeConverters(MoshiTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun launchesDao(): LaunchesDao

}
