package com.alfaazplus.sunnah.ui.utils.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.models.HadithOfTheDay
import com.alfaazplus.sunnah.db.entities.v2.HadithBlockType
import com.alfaazplus.sunnah.helpers.HadithHelper
import com.alfaazplus.sunnah.repository.hadith.HadithRepository2
import com.alfaazplus.sunnah.ui.activities.MainActivity
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.utils.notification.NotificationUtils.CHANNEL_ID_HOTD
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class HadithOfTheDayWorker @AssistedInject constructor(
    @Assisted
    context: Context,
    @Assisted
    params: WorkerParameters,
    private val repo: HadithRepository2,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val hotd = HadithHelper.getHadithOfTheDay(repo) ?: return@withContext Result.failure()

        sendNotification(hotd)

        return@withContext Result.success()
    }

    private fun sendNotification(hotd: HadithOfTheDay) {
        val notificationId = 10
        val context = applicationContext
        val blocks = hotd.hwc.contents.firstOrNull { it.lang == "en" }?.blocks ?: return

        val manager = ContextCompat.getSystemService(
            context, NotificationManager::class.java
        ) ?: return

        val hadithText = buildString {
            blocks.forEach {
                if ((it.type == HadithBlockType.NARRATOR || it.type == HadithBlockType.MATN) && !it.text.isNullOrEmpty()) {
                    appendLine(it.text.parseAsHtml())
                }
            }
        }

        val hadithReference = "${hotd.collectionName} : ${hotd.hwc.hadith.number}"

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(
                Keys.NAV_DESTINATION,
                Routes.READER.args(
                    hotd.hwc.collectionId,
                    hotd.hwc.hadithId,
                ),
            )
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )


        val notification = NotificationCompat
            .Builder(applicationContext, CHANNEL_ID_HOTD)
            .setContentTitle(context.getString(R.string.hadith_of_the_day))
            .setContentText(hadithText)
            .setStyle(
                NotificationCompat
                    .BigTextStyle()
                    .bigText(hadithText)
            )
            .setSubText(hadithReference)
            .setSmallIcon(R.drawable.logo_icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(notificationId, notification)
    }
}
