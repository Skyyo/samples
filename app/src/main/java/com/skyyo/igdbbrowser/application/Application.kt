package com.skyyo.igdbbrowser.application

import android.app.Application
import com.github.venom.Venom
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        Venom.createInstance(this).apply {
            initialize()
            start()
        }
    }

}