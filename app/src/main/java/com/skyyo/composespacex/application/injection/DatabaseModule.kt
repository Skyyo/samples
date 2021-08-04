package com.skyyo.composespacex.application.injection

import android.content.Context
import androidx.room.Room
import com.skyyo.composespacex.application.persistance.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext ctx: Context) =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "space-x-compose-database")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideLaunchesDao(appDatabase: AppDatabase) = appDatabase.launchesDao()
}
