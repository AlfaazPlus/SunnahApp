package com.alfaazplus.sunnah.ui.viewModels

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfaazplus.sunnah.repository.hadith.HadithRepository2
import com.alfaazplus.sunnah.repository.userdata.UserRepository
import com.alfaazplus.sunnah.ui.components.reader.HadithActions
import com.alfaazplus.sunnah.ui.models.ReaderLayoutItem
import com.alfaazplus.sunnah.ui.utils.preferences.HadithLayout
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.reader.ReaderChangeManager
import com.alfaazplus.sunnah.ui.utils.reader.ReaderItemsBuilder
import com.alfaazplus.sunnah.ui.utils.reader.ReaderPreparedData
import com.alfaazplus.sunnah.ui.utils.text.ComposeUiConfig
import com.alfaazplus.sunnah.ui.utils.text.TextBuilderParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val repo: HadithRepository2,
    private val userRepo: UserRepository,
) : ViewModel() {

    val layoutMode = ReaderPreferences
        .hadithLayoutFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

    var selectedNavigationTabIndex = mutableIntStateOf(1)

    private val _activeBookId = MutableStateFlow<String?>(null)
    val activeBookId: StateFlow<String?> = _activeBookId.asStateFlow()

    private val _activeHadithId = MutableStateFlow<String?>(null)
    val activeHadithId: StateFlow<String?> = _activeHadithId.asStateFlow()

    val books = _activeBookId
        .map {
            if (it != null) {
                repo.loadSisterBooksFromBookId(it)
            } else {
                emptyList()
            }
        }
        .distinctUntilChanged()
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList(),
        )

    private val _preparedData = MutableStateFlow<ReaderPreparedData?>(null)
    val preparedData: StateFlow<ReaderPreparedData?> = _preparedData.asStateFlow()

    private val _navigateToHadith = MutableStateFlow<String?>(null)
    val navigateToHadith: StateFlow<String?> = _navigateToHadith.asStateFlow()

    private val initReaderMutex = Mutex()

    /**
     * Collects UI-driving prefs and rebuilds reader content. Intended to run only while this
     * screen is visible.
     */
    suspend fun observeChanges(
        uiConfig: ComposeUiConfig,
        hadithActions: HadithActions,
    ) {
        combine(
            _activeBookId,
            ReaderChangeManager.changeFlow(),
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

    suspend fun initReaderIfNeeded(bookId: String, startHadithId: String? = null) {
        initReaderMutex.withLock {
            if (_activeBookId.value != bookId) {
                _activeBookId.value = bookId
            }

            startHadithId?.let { requestHadithNavigation(it) }
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

    fun handleHadithLayoutTransition(_to: HadithLayout) {
        val hadithId = _activeHadithId.value ?: return

        // reset if any
        consumeHadithNavigation()
        requestHadithNavigation(hadithId)
    }

    fun requestHadithNavigation(hadithId: String) {
        _navigateToHadith.value = hadithId
    }

    fun consumeHadithNavigation() {
        val navigatedHadithId = _navigateToHadith.value

        if (navigatedHadithId != null) {
            _activeHadithId.value = navigatedHadithId
        }

        _navigateToHadith.value = null
    }

    fun updateLastKnownHadith(index: Int) {
        val items = _preparedData.value?.items ?: return
        var hadithItem: ReaderLayoutItem.HadithUI? = null

        var target = index
        while (target <= items.lastIndex) {
            val item = items[target]

            if (item is ReaderLayoutItem.HadithUI) {
                hadithItem = item
                break
            } else {
                target++
            }
        }

        if (hadithItem != null) {
            _activeHadithId.value = hadithItem.hadithId
        }
    }

    fun saveReadHistory() {
        val hadithId = _activeHadithId.value ?: return

        viewModelScope.launch {
            userRepo.saveReadHistory(
                hadithId = hadithId,
            )
        }
    }
}
