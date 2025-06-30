package com.alfaazplus.sunnah.ui.utils.workers

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.models.HadithOfTheDay
import com.alfaazplus.sunnah.helpers.HadithHelper
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.ui.utils.notification.NotificationUtils.CHANNEL_ID_HOTD
import com.alfaazplus.sunnah.ui.utils.text.toAnnotatedString
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class HadithOfTheDayWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repo: HadithRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val hotd = HadithHelper.getHadithOfTheDay(repo) ?: return@withContext Result.failure()

        sendNotification(hotd)

        return@withContext Result.success()
    }

    private fun sendNotification(hotd: HadithOfTheDay) {
        val context = applicationContext

        val manager = ContextCompat.getSystemService(
            context, NotificationManager::class.java
        ) ?: return

        val notification = NotificationCompat
            .Builder(applicationContext, CHANNEL_ID_HOTD)
            .setContentTitle(context.getString(R.string.hadith_of_the_day))
            .setContentText(
                hotd.translation.hadithText
                    .parseAsHtml()
                    .toAnnotatedString()
            )
            .setSmallIcon(android.R.drawable.ic_dialog_info) // todo: logo
            .build()

        manager.notify(10, notification)
    }
}
