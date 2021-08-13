package com.skyyo.igdbbrowser.application.persistance.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skyyo.igdbbrowser.application.models.remote.Launch
import com.skyyo.igdbbrowser.application.models.remote.LaunchSite
import com.skyyo.igdbbrowser.application.models.remote.Rocket

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
