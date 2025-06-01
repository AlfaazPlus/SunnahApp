package com.alfaazplus.sunnah.ui.utils.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.models.HadithOfTheDay
import com.alfaazplus.sunnah.helpers.HadithHelper
import com.alfaazplus.sunnah.repository.hadith.HadithRepositoryImpl
import com.alfaazplus.sunnah.ui.utils.notification.NOTIFICATION_HOTD_CHANNEL_ID
import com.alfaazplus.sunnah.ui.utils.notification.NOTIFICATION_HOTD_CHANNEL_NAME
import com.alfaazplus.sunnah.ui.utils.notification.NOTIFICATION_HOTD_ID
import com.alfaazplus.sunnah.ui.utils.text.toAnnotatedString
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class HadithOfTheDayWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repo: HadithRepositoryImpl,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val hotd = HadithHelper.getHadithOfTheDay(repo) ?: return@withContext Result.failure()

        sendNotification(hotd)

        return@withContext Result.success()
    }

    private fun sendNotification(hotd: HadithOfTheDay) {
        val context = applicationContext
        val channelId = NOTIFICATION_HOTD_CHANNEL_ID

        val manager = ContextCompat.getSystemService(
            context, NotificationManager::class.java
        ) ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    channelId, NOTIFICATION_HOTD_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
                )
            )
        }

        val notification = NotificationCompat
            .Builder(applicationContext, channelId)
            .setContentTitle(context.getString(R.string.hadith_of_the_day))
            .setContentText(
                hotd.translation.hadithText
                    .parseAsHtml()
                    .toAnnotatedString()
            )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        manager.notify(NOTIFICATION_HOTD_ID, notification)
    }
}
