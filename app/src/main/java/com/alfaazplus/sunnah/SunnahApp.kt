package com.alfaazplus.sunnah

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.alfaazplus.sunnah.api.DownloadSourceUtils
import com.alfaazplus.sunnah.ui.utils.extended.ExceptionHandler
import com.alfaazplus.sunnah.ui.utils.notification.NotificationUtils
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import dagger.hilt.android.HiltAndroidApp
import java.io.File
import javax.inject.Inject


@HiltAndroidApp
class SunnahApp : Application(), Configuration.Provider {
    companion object {
        lateinit var appFilesDir: File
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun attachBaseContext(base: Context) {
        beforeAttachBaseContext(base)
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()

        DataStoreManager.init(this)
        DownloadSourceUtils.resetDownloadSourceBaseUrl()
        NotificationUtils.createNotificationChannels(this)

        // Handler for uncaught exceptions
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))
    }

    override fun getWorkManagerConfiguration() = Configuration
        .Builder()
        .setWorkerFactory(workerFactory)
        .build()

    private fun beforeAttachBaseContext(base: Context) {
        appFilesDir = base.filesDir
    }
}