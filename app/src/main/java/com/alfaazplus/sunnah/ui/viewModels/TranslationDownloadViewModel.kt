package com.alfaazplus.sunnah.ui.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.ui.utils.managers.ResourceDownloadStatus
import com.alfaazplus.sunnah.ui.utils.managers.TranslationDownloadManager
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
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

        val items = languages.map { translation ->
            val isDownloaded = downloadedCodes.contains(translation.langCode)

            TranslationUiModel(
                id = translation.langCode,
                title = translation.label,
                isDownloaded = isDownloaded,
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
        if (!selectedRow.isDownloaded) return

        viewModelScope.launch {
            ReaderPreferences.setHadithTranslation(id)
        }
    }

    fun startDownload(id: String) {
        val id = _uiState.value.rows.firstOrNull { it.id == id }?.id ?: return

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
