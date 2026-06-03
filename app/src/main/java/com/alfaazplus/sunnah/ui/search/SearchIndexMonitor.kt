package com.alfaazplus.sunnah.ui.search

import android.content.Context
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

object SearchIndexMonitor {
    fun isIndexingFlow(context: Context): Flow<Boolean> = callbackFlow {
        val workManager = WorkManager.getInstance(context.applicationContext)
        val liveData = workManager.getWorkInfosByTagLiveData(SearchIndexScheduler.INDEXING_TAG)

        val observer = Observer<List<WorkInfo>> { workInfos ->
            trySend(workInfos.any { it.state.isActive })
        }

        liveData.observeForever(observer)
        awaitClose { liveData.removeObserver(observer) }
    }.distinctUntilChanged()

    private val WorkInfo.State.isActive: Boolean
        get() = this == WorkInfo.State.ENQUEUED || this == WorkInfo.State.RUNNING
}
