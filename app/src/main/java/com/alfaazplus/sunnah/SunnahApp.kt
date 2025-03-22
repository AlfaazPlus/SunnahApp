package com.alfaazplus.sunnah

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.alfaazplus.sunnah.api.DownloadSourceUtils
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SunnahApp : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        DataStoreManager.init(this)
        DownloadSourceUtils.resetDownloadSourceBaseUrl(this)
        // Handler for uncaught exceptions
        // Thread.setDefaultUncaughtExceptionHandler(CustomExceptionHandler(this))
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}