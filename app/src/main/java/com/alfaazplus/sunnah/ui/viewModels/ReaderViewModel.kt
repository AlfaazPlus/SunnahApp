package com.alfaazplus.sunnah.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfaazplus.sunnah.repository.hadith.HadithRepository2
import com.alfaazplus.sunnah.repository.userdata.UserRepository
import com.alfaazplus.sunnah.ui.components.reader.HadithActions
import com.alfaazplus.sunnah.ui.utils.reader.ReaderChangeManager
import com.alfaazplus.sunnah.ui.utils.reader.ReaderItemsBuilder
import com.alfaazplus.sunnah.ui.utils.reader.ReaderPreparedData
import com.alfaazplus.sunnah.ui.utils.text.ComposeUiConfig
import com.alfaazplus.sunnah.ui.utils.text.TextBuilderParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val repo: HadithRepository2,
    private val userRepo: UserRepository,
) : ViewModel() {

    private val _activeBookId = MutableStateFlow<String?>(null)
    val activeBookId: StateFlow<String?> = _activeBookId.asStateFlow()

    private val _preparedData = MutableStateFlow<ReaderPreparedData?>(null)
    val preparedData: StateFlow<ReaderPreparedData?> = _preparedData.asStateFlow()

    private val _navigateToHadith = MutableStateFlow<String?>(null)
    val navigateToHadith: StateFlow<String?> = _navigateToHadith.asStateFlow()

    /**
     * Collects UI-driving prefs and rebuilds reader content. Intended to run only while this
     * screen is visible.
     */
    suspend fun observeChanges(
        uiConfig: ComposeUiConfig,
        hadithActions: HadithActions,
    ) {
        combine(
            activeBookId, ReaderChangeManager.changeFlow()
        ) { bookId, config ->
            Pair(bookId, config)
        }.collectLatest { (bookId, config) ->
            val params = TextBuilderParams(
                uiConfig = uiConfig,
                hadithActions = hadithActions,
                arabicSizePercent = config.txtSizePercentArabic,
                translationSizePercent = config.txtSizePercentTranslation,
                hadithTextOption = config.hadithTextOption,
                isSanadEnabled = config.isSanadEnabled,
                isSerifFontStyle = config.isSerifFontStyle,
            )

            if (bookId == null) return@collectLatest

            buildItems(
                bookId = bookId,
                params = params,
            )
        }
    }

    private suspend fun buildItems(
        bookId: String,
        params: TextBuilderParams,
    ) {
        _preparedData.value = ReaderItemsBuilder.build(
            repo,
            bookId,
            params,
        )
    }

    fun consumeNavigation(): String? {
        val current = _navigateToHadith.value
        _navigateToHadith.value = null
        return current
    }

    fun saveReadHistory(currentHadithId: String) {
        viewModelScope.launch {
            userRepo.saveReadHistory(
                hadithId = currentHadithId,
            )
        }
    }
}
