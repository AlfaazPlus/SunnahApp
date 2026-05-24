package com.alfaazplus.sunnah.ui.viewModels

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.repository.userdata.UserRepository
import com.alfaazplus.sunnah.ui.components.reader.HadithActions
import com.alfaazplus.sunnah.ui.models.ReaderLayoutItem
import com.alfaazplus.sunnah.ui.utils.preferences.HadithLayout
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.reader.ChangeConfig
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

private const val READER_PAGE_SIZE = 40
private const val READER_PREFETCH_DISTANCE = 8

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val repo: HadithRepository,
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
        .combine(ReaderPreferences.hadithTranslationFlow()) { bookId, langCode ->
            if (bookId != null) {
                repo.loadSisterBooksFromBookId(bookId, langCode)
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

    val hadithNavigationItems = _activeBookId
        .map { bookId ->
            if (bookId != null) {
                repo.dao.getHadithNavigationItemsForBook(bookId)
            } else {
                emptyList()
            }
        }
        .distinctUntilChanged()
        .stateIn(
            viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    private val _preparedData = MutableStateFlow<ReaderPreparedData?>(null)
    val preparedData: StateFlow<ReaderPreparedData?> = _preparedData.asStateFlow()

    private val _navigateToHadith = MutableStateFlow<String?>(null)
    val navigateToHadith: StateFlow<String?> = _navigateToHadith.asStateFlow()

    private val initReaderMutex = Mutex()
    private val pageLoadMutex = Mutex()

    private var activeBuildParams: TextBuilderParams? = null
    private var lastBuiltChangeConfig: ChangeConfig? = null
    private var nextPageOffset: Int = 0
    private var isPageLoadComplete: Boolean = false

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
                translationId = config.selectedTranslationLangCode,
                uiConfig = uiConfig,
                hadithActions = hadithActions,
                arabicSizePercent = config.txtSizePercentArabic,
                translationSizePercent = config.txtSizePercentTranslation,
                hadithTextOption = config.hadithTextOption,
                isSanadEnabled = config.isSanadEnabled,
                isSerifFontStyle = config.isSerifFontStyle,
            )

            if (bookId == null) return@collectLatest

            val currentData = _preparedData.value
            if (currentData != null && currentData.bookId == bookId && lastBuiltChangeConfig == config) {
                return@collectLatest
            }

            rebuildItems(
                bookId = bookId,
                params = params,
                changeConfig = config,
            )
        }
    }

    suspend fun initReaderIfNeeded(bookId: String, startHadithId: String? = null) {
        initReaderMutex.withLock {
            startHadithId?.let { requestHadithNavigation(it) }

            if (_activeBookId.value != bookId) {
                _activeBookId.value = bookId
            }
        }
    }

    private suspend fun rebuildItems(
        bookId: String,
        params: TextBuilderParams,
        changeConfig: ChangeConfig,
    ) {
        pageLoadMutex.withLock {
            activeBuildParams = params
            lastBuiltChangeConfig = changeConfig
            _preparedData.value = null

            val targetHadithId = _navigateToHadith.value ?: _activeHadithId.value
            if (targetHadithId != null) {
                loadPageContainingHadithLocked(
                    bookId = bookId,
                    hadithId = targetHadithId,
                    params = params,
                )
            } else {
                loadPageFromOffsetLocked(
                    bookId = bookId,
                    params = params,
                    offset = 0,
                    replaceCurrentItems = true,
                )
            }
        }
    }

    fun loadMoreItemsIfNeeded(lastVisibleIndex: Int) {
        val data = _preparedData.value ?: return
        if (data.isComplete || isPageLoadComplete) return
        if (data.items.lastIndex - lastVisibleIndex > READER_PREFETCH_DISTANCE) return

        val params = activeBuildParams ?: return

        viewModelScope.launch {
            pageLoadMutex.withLock {
                val activeBookId = _activeBookId.value ?: return@withLock
                if (activeBookId != data.bookId) return@withLock
                if (isPageLoadComplete) return@withLock

                loadNextPageLocked(activeBookId, params)
            }
        }
    }

    fun loadPageContainingHadithIfNeeded(hadithId: String) {
        val data = _preparedData.value ?: return

        val isAlreadyLoaded = data.items.any { item ->
            item is ReaderLayoutItem.HadithUI && item.hadithId == hadithId
        }
        if (isAlreadyLoaded) return

        val params = activeBuildParams ?: return

        viewModelScope.launch {
            pageLoadMutex.withLock {
                val activeBookId = _activeBookId.value ?: return@withLock
                loadPageContainingHadithLocked(
                    bookId = activeBookId,
                    hadithId = hadithId,
                    params = params,
                )
            }
        }
    }

    private suspend fun loadNextPageLocked(
        bookId: String,
        params: TextBuilderParams,
    ) {
        loadPageFromOffsetLocked(
            bookId = bookId,
            params = params,
            offset = nextPageOffset,
            replaceCurrentItems = false,
        )
    }

    private suspend fun loadPageContainingHadithLocked(
        bookId: String,
        hadithId: String,
        params: TextBuilderParams,
    ) {
        val targetOffset = repo.dao.getHadithOffsetInBook(bookId, hadithId)
        val pageOffset = (targetOffset / READER_PAGE_SIZE) * READER_PAGE_SIZE

        loadPageFromOffsetLocked(
            bookId = bookId,
            params = params,
            offset = pageOffset,
            replaceCurrentItems = true,
        )
    }

    private suspend fun loadPageFromOffsetLocked(
        bookId: String,
        params: TextBuilderParams,
        offset: Int,
        replaceCurrentItems: Boolean,
    ) {
        val currentData = _preparedData.value
        val currentItems = if (replaceCurrentItems) emptyList() else currentData?.items.orEmpty()
        val emittedChapterIds = currentItems
            .filterIsInstance<ReaderLayoutItem.HadithUI>()
            .mapNotNull { it.chapterUi?.chapter?.chapter?.id }
            .toSet()

        val page = ReaderItemsBuilder.buildPage(
            repo = repo,
            bookId = bookId,
            params = params,
            offset = offset,
            limit = READER_PAGE_SIZE,
            emittedChapterIds = emittedChapterIds,
        )

        if (page == null) {
            isPageLoadComplete = true
            return
        }

        nextPageOffset = page.nextOffset
        isPageLoadComplete = page.nextOffset >= page.totalHadithCount || page.items.isEmpty()

        _preparedData.value = ReaderPreparedData(
            bookId = bookId,
            items = currentItems + page.items,
            totalHadithCount = page.totalHadithCount,
            isComplete = isPageLoadComplete,
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
