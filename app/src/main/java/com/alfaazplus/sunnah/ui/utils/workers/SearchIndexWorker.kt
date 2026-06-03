package com.alfaazplus.sunnah.ui.utils.workers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.repository.hadith.SearchRepository
import com.alfaazplus.sunnah.ui.activities.MainActivity
import com.alfaazplus.sunnah.ui.search.SearchIndexScheduler
import com.alfaazplus.sunnah.ui.utils.notification.NotificationUtils
import com.alfaazplus.sunnah.ui.utils.notification.NotificationUtils.createForegroundInfoFallback
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
    private val ctx: Context,
    @Assisted
    params: WorkerParameters,
    private val searchRepo: SearchRepository,
) : CoroutineWorker(ctx, params) {
    companion object {
        const val KEY_MODE = "mode"
        const val KEY_LANG = "lang"
        const val KEY_HADITH_VERSION = "hadith_version"
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo()
    }

    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo())

        return withContext(Dispatchers.IO) {
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

                    val hadithVersion = inputData.getInt(KEY_HADITH_VERSION, -1)

                    if (hadithVersion >= 0) {
                        SearchIndexScheduler.markIndexUpToDate(applicationContext, hadithVersion)
                    }

                    Result.success()
                }

                else -> Result.failure()
            }
        }
    }

    private suspend fun buildIndexForLangIfNeeded(langCode: String): Unit = withContext(Dispatchers.IO) {
        searchRepo.buildIndexForLangIfNeeded(langCode)
    }

    private suspend fun removeLang(langCode: String) = withContext(Dispatchers.IO) {
        searchRepo.removeLang(langCode)
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val mode = SearchIndexSyncMode.fromValue(inputData.getString(KEY_MODE)) ?: return createForegroundInfoFallback(ctx)

        val langCode = inputData.getString(KEY_LANG)

        val notificationId = notificationIdFor(mode, langCode)

        val builder = NotificationCompat
            .Builder(ctx, NotificationUtils.CHANNEL_ID_DEFAULT)
            .apply {
                setAutoCancel(false)
                setOngoing(true)
                setShowWhen(false)
                setSilent(true)
                setOnlyAlertOnce(true)
                setSmallIcon(R.drawable.logo_icon)
                setContentTitle("Indexing hadith data")
                setCategory(NotificationCompat.CATEGORY_PROGRESS)
                setProgress(100, 0, true)
                setContentIntent(
                    PendingIntent.getActivity(
                        ctx,
                        notificationId,
                        Intent(ctx, MainActivity::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                    )
                )
            }

        return ForegroundInfo(
            notificationId,
            builder.build(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
        )
    }

    private fun notificationIdFor(mode: SearchIndexSyncMode, langCode: String?): Int {
        return when (mode) {
            SearchIndexSyncMode.MODE_SINGLE_LANG -> "search_index:$langCode".hashCode()
            SearchIndexSyncMode.MODE_ALL -> "search_index:all".hashCode()
            SearchIndexSyncMode.MODE_REMOVE_LANG -> "search_index:remove:$langCode".hashCode()
        }
    }
}
