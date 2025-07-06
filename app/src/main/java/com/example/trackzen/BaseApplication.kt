package com.example.trackzen

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any libraries or components here
        Timber.plant(Timber.DebugTree())


    }

}