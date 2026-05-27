package com.alfaazplus.sunnah.ui.utils.managers

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.alfaazplus.sunnah.ui.utils.workers.TranslationDownloadWorker
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

object TranslationDownloadManager {
    private const val TAG = "download_translation"
    private const val ITEM_TAG_PREFIX = "DownloadTranslation:"

    private val downloadStates = MutableLiveData<Map<String, WorkInfo>>(emptyMap())

    fun initialize(context: Context) {
        WorkManager
            .getInstance(context)
            .getWorkInfosByTagLiveData(TAG)
            .observeForever { workInfos ->
                val map = mutableMapOf<String, WorkInfo>()
                for (info in workInfos) {
                    if (info.state.isFinished) continue
                    val idTag = info.tags.firstOrNull { it.startsWith(ITEM_TAG_PREFIX) } ?: continue
                    val id = idTag.substringAfter(ITEM_TAG_PREFIX)
                    map[id] = info
                }
                downloadStates.postValue(map)
            }
    }

    fun startDownload(context: Context, translationId: String) {
        val data = workDataOf("id" to translationId)
        val itemTag = "${ITEM_TAG_PREFIX}${translationId}"

        val request = OneTimeWorkRequestBuilder<TranslationDownloadWorker>()
            .setInputData(data)
            .addTag(TAG)
            .addTag(itemTag)
            .build()

        val wm = WorkManager.getInstance(context)
        wm.enqueueUniqueWork(
            itemTag, ExistingWorkPolicy.REPLACE, request
        )

        observeWork(context, translationId, request.id)
    }

    fun stopDownload(context: Context, id: String) {
        WorkManager
            .getInstance(context)
            .cancelUniqueWork("${ITEM_TAG_PREFIX}$id")
    }

    private fun observeWork(context: Context, id: String, workId: UUID) {
        val wm = WorkManager.getInstance(context)
        val liveData = wm.getWorkInfoByIdLiveData(workId)
        val observer = object : Observer<WorkInfo?> {
            override fun onChanged(value: WorkInfo?) {
                if (value != null) {
                    updateState(id, value)
                    if (value.state.isFinished) {
                        liveData.removeObserver(this)
                    }
                }
            }
        }

        liveData.observeForever(observer)
    }

    private fun updateState(id: String, info: WorkInfo) {
        val current = downloadStates.value?.toMutableMap() ?: mutableMapOf()
        current[id] = info
        downloadStates.postValue(current)
    }

    private fun removeState(id: String) {
        val current = downloadStates.value?.toMutableMap() ?: mutableMapOf()
        current.remove(id)
        downloadStates.postValue(current)
    }

    fun observeDownloadsAsFlow(): Flow<Pair<String, ResourceDownloadStatus>> = callbackFlow {
        val observer = Observer<Map<String, WorkInfo>> { map ->
            for ((id, workInfo) in map) {
                val status = when (workInfo.state) {
                    WorkInfo.State.ENQUEUED -> ResourceDownloadStatus.Started
                    WorkInfo.State.RUNNING -> {
                        val progress = workInfo.progress.getInt("progress", 0)
                        ResourceDownloadStatus.InProgress(progress)
                    }

                    WorkInfo.State.SUCCEEDED -> {
                        removeState(id)
                        ResourceDownloadStatus.Completed
                    }

                    WorkInfo.State.FAILED -> {
                        val error = workInfo.outputData.getString("error")
                        removeState(id)
                        ResourceDownloadStatus.Failed(error)
                    }

                    WorkInfo.State.CANCELLED -> {
                        removeState(id)
                        ResourceDownloadStatus.Cancelled
                    }

                    else -> null
                }

                if (status != null) {
                    trySend(id to status)
                }
            }
        }

        downloadStates.observeForever(observer)
        awaitClose { downloadStates.removeObserver(observer) }
    }
}

sealed class ResourceDownloadStatus {
    object Idle : ResourceDownloadStatus()
    object Started : ResourceDownloadStatus()
    data class InProgress(val progress: Int) : ResourceDownloadStatus()
    object Completed : ResourceDownloadStatus()
    data class Failed(val error: String?) : ResourceDownloadStatus()
    object Cancelled : ResourceDownloadStatus()
}
