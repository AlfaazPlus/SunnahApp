package com.alfaazplus.sunnah.ui.viewModels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.alfaazplus.sunnah.db.entities.scholars.Scholar
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.ui.search.BookSearchQuickResult
import com.alfaazplus.sunnah.ui.search.BooksSearchResult
import com.alfaazplus.sunnah.ui.search.HadithSearchQuickResult
import com.alfaazplus.sunnah.ui.search.HadithSearchResult
import com.alfaazplus.sunnah.ui.search.SearchFilters
import com.alfaazplus.sunnah.ui.search.SearchFiltersStore
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
open class SearchViewModel @Inject constructor(
    private val repo: HadithRepository,
) : ViewModel() {
    var primaryColor = Color.Unspecified
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _currentFilters = MutableStateFlow(SearchFiltersStore.read())
    val currentFilters = _currentFilters.asStateFlow()

    val hadithsSearchResults: Flow<PagingData<HadithSearchResult>> = combine(
        _currentFilters,
        _searchQuery
            .debounce(300)
            .distinctUntilChanged(),
        ReaderPreferences.hadithTranslationFlow(),
    ) { filters, query, langCode ->
        Triple(filters, query, langCode)
    }
        .flatMapLatest { (filters, query, langCode) ->
            repo.searchHadiths(
                query,
                collectionIds = filters.selectedCollections?.takeIf { it.isNotEmpty() },
                primaryColor,
                langCode,
            )
        }
        .cachedIn(viewModelScope)
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = PagingData.empty(),
        )


    val booksSearchResults: Flow<PagingData<BooksSearchResult>> = combine(
        _currentFilters,
        _searchQuery
            .debounce(300)
            .distinctUntilChanged(),
        ReaderPreferences.hadithTranslationFlow(),
    ) { filters, query, langCode ->
        Triple(filters, query, langCode)
    }
        .flatMapLatest { (filters, query, langCode) ->
            repo.searchBooks(
                query,
                collectionIds = filters.selectedCollections?.takeIf { it.isNotEmpty() },
                langCode,
            )
        }
        .cachedIn(viewModelScope)
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = PagingData.empty(),
        )

    val scholarsSearchResults: Flow<PagingData<Scholar>> = _searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            repo.searchScholars(query)
        }
        .cachedIn(viewModelScope)
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = PagingData.empty(),
        )

    val quickHadithResults: StateFlow<List<HadithSearchQuickResult>> = combine(
        _searchQuery
            .debounce(300)
            .distinctUntilChanged(),
        ReaderPreferences.hadithTranslationFlow(),
    ) { query, langCode -> query to langCode }
        .mapLatest { (query, langCode) ->
            repo.getQuickHadithSearchResults(query, langCode)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList(),
        )

    val quickBookResults: StateFlow<List<BookSearchQuickResult>> = combine(
        _searchQuery
            .debounce(300)
            .distinctUntilChanged(),
        ReaderPreferences.hadithTranslationFlow(),
    ) { query, langCode -> query to langCode }
        .mapLatest { (query, langCode) ->
            repo.getQuickBookSearchResults(query, langCode)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList(),
        )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun setFilters(filters: SearchFilters) {
        _currentFilters.value = filters

        viewModelScope.launch {
            SearchFiltersStore.write(filters)
        }
    }
}
