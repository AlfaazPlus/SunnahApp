package com.alfaazplus.sunnah.ui.viewModels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.ui.models.BooksSearchResult
import com.alfaazplus.sunnah.ui.models.HadithSearchResult
import com.alfaazplus.sunnah.ui.models.ScholarsSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
open class SearchViewModel @Inject constructor(
    private val repo: HadithRepository,
) : ViewModel() {
    var primaryColor = Color.Unspecified
    private val _searchQuery = MutableStateFlow("")
    private var _searchCollectionIds = MutableStateFlow<Set<Int>?>(null)

    val searchQuery: StateFlow<String> = _searchQuery
    val searchCollectionIds: StateFlow<Set<Int>?> = _searchCollectionIds

    val hadithsSearchResults: Flow<PagingData<HadithSearchResult>> = combine(
        _searchCollectionIds
            .debounce(300)
            .distinctUntilChanged(),
        _searchQuery
            .debounce(300)
            .distinctUntilChanged(),
    ) { collectionIds, query -> Pair(collectionIds, query) }
        .flatMapLatest { (collectionIds, query) ->
            repo.searchHadiths(query, collectionIds, primaryColor)
        }
        .cachedIn(viewModelScope)
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = PagingData.empty(),
        )


    val booksSearchResults: Flow<PagingData<BooksSearchResult>> = combine(
        searchCollectionIds,
        _searchQuery
            .debounce(300)
            .distinctUntilChanged(),
    ) { collectionIds, query -> Pair(collectionIds, query) }
        .flatMapLatest { (collectionIds, query) ->
            repo.searchBooks(query, collectionIds)
        }
        .cachedIn(viewModelScope)
        .stateIn(
            viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = PagingData.empty(),
        )

    private var scholarsSearchResults = MutableStateFlow<ScholarsSearchResult?>(null)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun applyFilters(
        searchCollectionIds: Set<Int>,
    ) {
        _searchCollectionIds.value = searchCollectionIds
    }
}
