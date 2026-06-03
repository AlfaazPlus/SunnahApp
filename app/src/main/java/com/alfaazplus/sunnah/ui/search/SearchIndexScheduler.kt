package com.alfaazplus.sunnah.ui.search

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.alfaazplus.sunnah.db.databases.SEARCH_INDEX_DB_VERSION
import com.alfaazplus.sunnah.ui.utils.workers.SearchIndexSyncMode
import com.alfaazplus.sunnah.ui.utils.workers.SearchIndexWorker
import java.util.concurrent.TimeUnit

object SearchIndexScheduler {
    const val WORK_TAG = "search_index_work"
    const val INDEXING_TAG = "search_index_indexing"
    private const val UNIQUE_SYNC = "search_index_sync_all"
    private const val PREFS_NAME = "search_index_prefs"
    private const val KEY_FTS_SCHEMA = "search_fts_schema"
    private const val KEY_INDEXED_HADITH_VERSION = "search_indexed_hadith_version"

    private fun workManager(context: Context): WorkManager = WorkManager.getInstance(context.applicationContext)

    private fun buildRequest(
        mode: SearchIndexSyncMode,
        langCode: String? = null,
        hadithDataVersion: Int? = null,
        backoffMs: Long? = null,
    ): OneTimeWorkRequest {
        val data = when {
            langCode != null -> {
                workDataOf(
                    SearchIndexWorker.KEY_MODE to mode.value,
                    SearchIndexWorker.KEY_LANG to langCode,
                )
            }

            hadithDataVersion != null -> {
                workDataOf(
                    SearchIndexWorker.KEY_MODE to mode.value,
                    SearchIndexWorker.KEY_HADITH_VERSION to hadithDataVersion,
                )
            }

            else -> {
                workDataOf(SearchIndexWorker.KEY_MODE to mode.value)
            }
        }

        return OneTimeWorkRequestBuilder<SearchIndexWorker>()
            .addTag(WORK_TAG)
            .apply {
                if (mode != SearchIndexSyncMode.MODE_REMOVE_LANG) {
                    addTag(INDEXING_TAG)
                }
            }
            .setInputData(data)
            .apply {
                backoffMs?.let {
                    setBackoffCriteria(BackoffPolicy.LINEAR, it, TimeUnit.MILLISECONDS)
                }

                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            }
            .build()
    }

    fun enqueueLang(context: Context, slug: String) {
        workManager(context).enqueue(
            buildRequest(
                SearchIndexSyncMode.MODE_SINGLE_LANG,
                langCode = slug,
                backoffMs = 30_000L,
            ),
        )
    }

    fun enqueueRemoveLang(context: Context, slug: String) {
        workManager(context).enqueue(
            buildRequest(
                SearchIndexSyncMode.MODE_REMOVE_LANG,
                langCode = slug,
            ),
        )
    }

    fun enqueueSyncAll(context: Context, hadithDataVersion: Int) {
        workManager(context).enqueueUniqueWork(
            UNIQUE_SYNC,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            buildRequest(
                SearchIndexSyncMode.MODE_ALL,
                hadithDataVersion = hadithDataVersion,
                backoffMs = 60_000L,
            ),
        )
    }

    fun scheduleSearchIndexIfNeeded(
        context: Context,
        hadithDataVersion: Int,
    ) {
        val sp = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val lastSchema = sp.getInt(KEY_FTS_SCHEMA, 0)
        val lastHadithVersion = sp.getInt(KEY_INDEXED_HADITH_VERSION, 0)

        val schemaChanged = lastSchema != SEARCH_INDEX_DB_VERSION
        val hadithDataChanged = lastHadithVersion != hadithDataVersion

        if (!schemaChanged && !hadithDataChanged) {
            return
        }

        enqueueSyncAll(context, hadithDataVersion)
    }

    fun markIndexUpToDate(context: Context, hadithDataVersion: Int) {
        context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit {
            putInt(KEY_FTS_SCHEMA, SEARCH_INDEX_DB_VERSION)
            putInt(KEY_INDEXED_HADITH_VERSION, hadithDataVersion)
        }
    }
}
