package com.alfaazplus.sunnah.ui.utils.workers

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.repository.hadith.SearchRepository
import com.alfaazplus.sunnah.ui.utils.notification.NotificationUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class SearchIndexSyncMode(val value: String) {
    MODE_ALL("all"),
    MODE_SINGLE_LANG("lang"),
    MODE_REMOVE_LANG("remove");

    companion object {
        fun fromValue(value: String?): SearchIndexSyncMode? {
            return entries.find { it.value == value }
        }
    }
}

@HiltWorker
class SearchIndexWorker @AssistedInject constructor(
    @Assisted
    appContext: Context,
    @Assisted
    params: WorkerParameters,
    private val hadithRepo: HadithRepository,
    private val searchRepo: SearchRepository,
) : CoroutineWorker(appContext, params) {
    companion object {
        const val KEY_MODE = "mode"
        const val KEY_LANG = "lang"
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = NotificationCompat
            .Builder(
                applicationContext, NotificationUtils.CHANNEL_ID_DEFAULT
            )
            .setContentTitle("Indexing hadith data")
            .setContentText("Preparing...")
            .setSmallIcon(R.drawable.logo_icon)
            .setOngoing(true)
            .build()

        return ForegroundInfo(1001, notification)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        when (val mode = SearchIndexSyncMode.fromValue(inputData.getString(KEY_MODE))) {
            SearchIndexSyncMode.MODE_REMOVE_LANG -> {
                val langCode = inputData.getString(KEY_LANG) ?: return@withContext Result.failure()

                removeLang(applicationContext, langCode)

                Result.success()
            }

            SearchIndexSyncMode.MODE_SINGLE_LANG -> {
                val slug = inputData.getString(KEY_LANG) ?: return@withContext Result.failure()

                buildIndexForLangIfNeeded(applicationContext, slug)

                Result.success()
            }

            SearchIndexSyncMode.MODE_ALL -> {
                // todo: all
                Result.success()
            }

            else -> Result.failure()
        }
    }

    private suspend fun buildIndexForLangIfNeeded(context: Context, langCode: String): Unit = withContext(Dispatchers.IO) {

    }

    private suspend fun removeLang(context: Context, langCode: String) = withContext(Dispatchers.IO) {

    }

}
