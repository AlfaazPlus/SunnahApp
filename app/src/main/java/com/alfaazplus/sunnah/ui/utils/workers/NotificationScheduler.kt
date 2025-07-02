package com.alfaazplus.sunnah.ui.utils.workers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object HadithOfTheDayScheduler {
    fun scheduleDailyNotification(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<HadithOfTheDayWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
        ).build()

        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                "hotd_reminder",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest,
            )
    }

    fun cancelDailyNotification(context: Context) {
        WorkManager
            .getInstance(context)
            .cancelUniqueWork("hotd_reminder")
    }
}
