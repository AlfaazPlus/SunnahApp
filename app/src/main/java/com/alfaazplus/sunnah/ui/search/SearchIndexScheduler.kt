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
    private const val UNIQUE_SYNC = "search_index_sync_all"

    private fun workManager(context: Context): WorkManager = WorkManager.getInstance(context.applicationContext)

    private fun buildRequest(
        mode: SearchIndexSyncMode,
        langCode: String? = null,
        backoffMs: Long? = null,
    ): OneTimeWorkRequest {
        val data = if (langCode != null) {
            workDataOf(
                SearchIndexWorker.KEY_MODE to mode.value,
                SearchIndexWorker.KEY_LANG to langCode,
            )
        } else {
            workDataOf(SearchIndexWorker.KEY_MODE to mode.value)
        }

        return OneTimeWorkRequestBuilder<SearchIndexWorker>()
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

    fun enqueueSyncAll(context: Context) {
        workManager(context).enqueueUniqueWork(
            UNIQUE_SYNC,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            buildRequest(
                SearchIndexSyncMode.MODE_ALL,
                backoffMs = 60_000L,
            ),
        )
    }


    fun scheduleSearchIndexIfNeeded(context: Context) {
        val sp = context.getSharedPreferences("search_index_prefs", MODE_PRIVATE)
        val lastSchema = sp.getInt("search_fts_schema", 0)

        if (lastSchema != SEARCH_INDEX_DB_VERSION) {
            sp.edit {
                putInt(
                    "search_fts_schema", SEARCH_INDEX_DB_VERSION
                )
            }

            enqueueSyncAll(context)
        }
    }
}
