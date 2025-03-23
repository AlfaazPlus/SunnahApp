package com.alfaazplus.sunnah.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.alfaazplus.sunnah.ui.utils.workers.DownloadCollectionWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DownloadCollectionViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {
    private val workManager = WorkManager.getInstance(application)

    private val _downloadStates = MutableStateFlow<Map<Int, WorkInfo.State>>(emptyMap())
    val downloadStates: StateFlow<Map<Int, WorkInfo.State>> = _downloadStates

    fun startDownload(collectionId: Int, onResult: (Int, Boolean, Data) -> Unit) {
        val data = workDataOf("collectionId" to collectionId)

        val workRequest = OneTimeWorkRequestBuilder<DownloadCollectionWorker>()
            .setInputData(data)
            .build()

        workManager.enqueueUniqueWork(
            "download_collection_$collectionId",
            ExistingWorkPolicy.KEEP,
            workRequest
        )

        observeWorkState(collectionId, workRequest.id, onResult)
    }

    private fun observeWorkState(collectionId: Int, workId: UUID, onResult: (Int, Boolean, Data) -> Unit) {
        workManager.getWorkInfoByIdLiveData(workId).observeForever { workInfo ->
            if (workInfo != null) {
                val newState = workInfo.state
                val currentStates = _downloadStates.value.toMutableMap()
                currentStates[collectionId] = newState
                _downloadStates.value = currentStates

                if (workInfo.state.isFinished) {
                    onResult(collectionId, workInfo.state == WorkInfo.State.SUCCEEDED, workInfo.outputData)
                }
            }
        }
    }
}
