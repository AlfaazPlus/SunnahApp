package com.alfaazplus.sunnah.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.ui.utils.app.ResourceUpdateManager
import com.alfaazplus.sunnah.ui.utils.app.ResourceUpdateState
import com.alfaazplus.sunnah.ui.utils.managers.ResourceDownloadStatus
import com.alfaazplus.sunnah.ui.utils.managers.TranslationDownloadManager
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.reader.TranslationManager
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils.AVAILABLE_TRANSLATIONS
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils.DEFAULT_TRANSLATION
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TranslationUiModel(
    val id: String,
    val title: String,
    val isDownloaded: Boolean,
    val hasUpdate: Boolean,
    val isComingSoon: Boolean = false,
)


data class TranslationDownloadUiState(
    val rows: List<TranslationUiModel> = emptyList(),
    val selectedTranslation: String? = null,
    val downloadStates: Map<String, ResourceDownloadStatus> = emptyMap(),
)


@HiltViewModel
class TranslationDownloadViewModel @Inject constructor(
    application: Application,
    private val repo: HadithRepository,
) : AndroidViewModel(application) {
    private val context get() = application

    private val _uiState = MutableStateFlow(TranslationDownloadUiState())
    val uiState = _uiState.asStateFlow()

    init {
        TranslationDownloadManager.initialize(context)
        observeSelection()
        observeDownloads()

        viewModelScope.launch {
            refreshRows()
            ResourceUpdateManager.checkAndPerformUpdates()
        }

        viewModelScope.launch {
            ResourceUpdateManager.updateState.collect { state ->
                if (state == ResourceUpdateState.COMPLETED || state == ResourceUpdateState.IDLE) {
                    refreshRows()
                }
            }
        }
    }

    private fun observeSelection() {
        viewModelScope.launch {
            ReaderPreferences
                .hadithTranslationFlow()
                .collect { selected ->
                    _uiState.update { it.copy(selectedTranslation = selected) }
                }
        }
    }

    private fun observeDownloads() {
        viewModelScope.launch {
            TranslationDownloadManager
                .observeDownloadsAsFlow()
                .collect { (id, status) ->
                    _uiState.update { state ->
                        val next = state.downloadStates.toMutableMap()
                        when (status) {
                            is ResourceDownloadStatus.Completed,
                            is ResourceDownloadStatus.Cancelled,
                                -> next.remove(id)

                            else -> next[id] = status
                        }
                        state.copy(downloadStates = next)
                    }

                    if (status is ResourceDownloadStatus.Completed) {
                        refreshRows()
                    }
                }
        }
    }

    private suspend fun refreshRows() {
        val languages = AVAILABLE_TRANSLATIONS

        val downloadedCodes = repo
            .getDownloadedTranslations(languages.map { it.langCode })
            .toSet()

        val resourceVersions = ResourceUpdateManager.getLocalVersions()

        val items = languages.map { translation ->
            val id = translation.langCode
            val isDownloaded = downloadedCodes.contains(id)
            val hasUpdate =
                isDownloaded && !TranslationUtils.isBuiltInTranslation(id) && resourceVersions != null && TranslationManager.isUpdateAvailable(
                    context, id, resourceVersions
                )

            TranslationUiModel(
                id = id,
                title = translation.label,
                isDownloaded = isDownloaded,
                hasUpdate = hasUpdate,
                isComingSoon = translation.isComingSoon,
            )
        }

        _uiState.update {
            it.copy(
                rows = items,
            )
        }
    }


    fun selectLanguage(id: String) {
        val selectedRow = _uiState.value.rows.firstOrNull { it.id == id } ?: return
        if (selectedRow.isComingSoon || !selectedRow.isDownloaded) return

        viewModelScope.launch {
            ReaderPreferences.setHadithTranslation(id)
        }
    }

    fun startDownload(id: String) {
        val row = _uiState.value.rows.firstOrNull { it.id == id } ?: return
        if (row.isComingSoon) return
        val id = row.id

        TranslationDownloadManager.startDownload(context, id)

        _uiState.update {
            it.copy(downloadStates = it.downloadStates + (id to ResourceDownloadStatus.Started))
        }
    }

    fun cancelDownload(id: String) {
        TranslationDownloadManager.stopDownload(context, id)

        _uiState.update {
            it.copy(downloadStates = it.downloadStates - id)
        }
    }

    fun deleteTranslation(id: String) {
        if (TranslationUtils.isBuiltInTranslation(id)) {
            return
        }

        viewModelScope.launch {
            repo.deleteTranslationData(id)
            ReaderPreferences.setHadithTranslation(DEFAULT_TRANSLATION.langCode)
            refreshRows()
        }
    }
}
