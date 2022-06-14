package com.skyyo.samples.application

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.github.venom.Venom
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class Application : Application(), Configuration.Provider {

    @Inject
    lateinit var factory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        Venom.createInstance(this).apply {
            initialize()
            start()
        }
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder().setWorkerFactory(factory).build()
}