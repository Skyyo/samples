package com.skyyo.igdbbrowser.application.injection

import com.skyyo.igdbbrowser.utils.eventDispatchers.UnauthorizedEventDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApplicationModule {

    @Singleton
    @Provides
    fun provideUnauthorizedEventDispatcher(): UnauthorizedEventDispatcher =
        UnauthorizedEventDispatcher()


}