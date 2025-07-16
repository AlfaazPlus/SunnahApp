package com.alfaazplus.sunnah.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.alfaazplus.sunnah.ui.utils.workers.DownloadCollectionWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

private const val TAG = "download_collection"

@HiltViewModel
class DownloadCollectionViewModel @Inject constructor(
    application: Application,
) : AndroidViewModel(application) {
    private val workManager = WorkManager.getInstance(application)

    private val _downloadStates = MutableStateFlow<Map<Int, WorkInfo>>(emptyMap())

    init {
        viewModelScope.launch {
            val infos = withContext(Dispatchers.IO) {
                workManager
                    .getWorkInfosByTag(TAG)
                    .get()
            }

            infos
                .filter { !it.state.isFinished }
                .forEach { workInfo -> // retrieve collectionId from workData
                    val tags = workInfo.tags
                    val collectionId = tags
                        .find { it.startsWith("DownloadCollection:") }
                        ?.substringAfter(":")
                        ?.toIntOrNull() ?: -1

                    if (collectionId != -1) {
                        setDownloadState(collectionId, workInfo)
                        observeWorkState(collectionId, workInfo.id)
                    }
                }
        }
    }

    fun startDownload(collectionId: Int) {
        val data = workDataOf("collectionId" to collectionId)

        val workRequest = OneTimeWorkRequestBuilder<DownloadCollectionWorker>()
            .setInputData(data)
            .addTag(TAG)
            .addTag("DownloadCollection:$collectionId")
            .build()

        workManager.enqueueUniqueWork(
            "DownloadCollection:$collectionId",
            ExistingWorkPolicy.KEEP,
            workRequest,
        )

        observeWorkState(collectionId, workRequest.id)
    }

    fun getDownloadStateFlow(collectionId: Int): Flow<WorkInfo?> {
        return _downloadStates
            .map { it[collectionId] }
            .distinctUntilChanged()
    }

    private fun observeWorkState(collectionId: Int, workId: UUID) {
        val observer = object : Observer<WorkInfo?> {
            override fun onChanged(value: WorkInfo?) {
                if (value != null) {
                    setDownloadState(collectionId, value)

                    if (value.state.isFinished) {
                        workManager
                            .getWorkInfoByIdLiveData(workId)
                            .removeObserver(this)
                    }
                }
            }
        }

        workManager
            .getWorkInfoByIdLiveData(workId)
            .observeForever(observer)
    }

    private fun setDownloadState(collectionId: Int, workInfo: WorkInfo) {
        val currentStates = _downloadStates.value.toMutableMap()
        currentStates[collectionId] = workInfo
        _downloadStates.value = currentStates
    }
}
