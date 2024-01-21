package com.alfaazplus.sunnah

import android.app.Application
import com.alfaazplus.sunnah.ui.utils.shared_preference.Preferences
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SunnahApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Preferences.init(this)
        // Handler for uncaught exceptions
        // Thread.setDefaultUncaughtExceptionHandler(CustomExceptionHandler(this))
    }
}