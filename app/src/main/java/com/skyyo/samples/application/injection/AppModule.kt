package com.skyyo.samples.application.injection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.skyyo.samples.features.userInteractionTrackingResult.UserIdlingSessionEventDispatcher
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideUserIdlingSessionEventDispatcher(): UserIdlingSessionEventDispatcher = UserIdlingSessionEventDispatcher()
}
