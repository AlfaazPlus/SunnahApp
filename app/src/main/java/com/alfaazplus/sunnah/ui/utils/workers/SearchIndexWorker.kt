package com.alfaazplus.sunnah.ui.utils.workers

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.alfaazplus.sunnah.R
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
        when (SearchIndexSyncMode.fromValue(inputData.getString(KEY_MODE))) {
            SearchIndexSyncMode.MODE_REMOVE_LANG -> {
                val langCode = inputData.getString(KEY_LANG) ?: return@withContext Result.failure()

                removeLang(langCode)

                Result.success()
            }

            SearchIndexSyncMode.MODE_SINGLE_LANG -> {
                val slug = inputData.getString(KEY_LANG) ?: return@withContext Result.failure()

                buildIndexForLangIfNeeded(slug)

                Result.success()
            }

            SearchIndexSyncMode.MODE_ALL -> {
                searchRepo.buildAllIndexes()
                Result.success()
            }

            else -> Result.failure()
        }
    }

    private suspend fun buildIndexForLangIfNeeded(langCode: String): Unit = withContext(Dispatchers.IO) {
        searchRepo.buildIndexForLangIfNeeded(langCode)
    }

    private suspend fun removeLang(langCode: String) = withContext(Dispatchers.IO) {
        searchRepo.removeLang(langCode)
    }

}
