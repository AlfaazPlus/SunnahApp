package com.alfaazplus.sunnah.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfaazplus.sunnah.db.models.userdata.UserBookmark
import com.alfaazplus.sunnah.db.models.userdata.UserCollection
import com.alfaazplus.sunnah.repository.userdata.UserRepository
import com.alfaazplus.sunnah.ui.models.userdata.UserBookmarkNormalized
import com.alfaazplus.sunnah.ui.models.userdata.UserCollectionItemNormalized
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDataViewModel @Inject constructor(
    private val repository: UserRepository,
) : ViewModel() {
    private val bookmarkCache = mutableMapOf<String, StateFlow<Boolean>>()

    val repo get() = repository

    val userCollections: StateFlow<List<UserCollection>> = repository
        .observeAllUserCollections()
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val allUserBookmarks: StateFlow<List<UserBookmarkNormalized>> = repository
        .observeAllUserBookmarks()
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val recentUserBookmarks: StateFlow<List<UserBookmark>> = repository
        .observeRecentUserBookmarks()
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    private val _collectionItems = MutableStateFlow<List<UserCollectionItemNormalized>>(emptyList())
    val collectionItems: StateFlow<List<UserCollectionItemNormalized>> = _collectionItems

    fun isBookmarked(
        hadithCollectionId: Int,
        hadithBookId: Int,
        hadithNumber: String,
    ): StateFlow<Boolean> {
        val key = "$hadithCollectionId-$hadithBookId-$hadithNumber"

        return bookmarkCache.getOrPut(key) {
            repository
                .observeUserBookmark(
                    hadithCollectionId,
                    hadithBookId,
                    hadithNumber,
                )
                .map { it != null }
                .stateIn(
                    viewModelScope,
                    started = SharingStarted.Eagerly,
                    initialValue = false,
                )
        }
    }

    fun loadCollectionItems(
        collectionId: Long,
    ) {
        viewModelScope.launch {
            repository
                .observeUserCollectionItems(collectionId)
                .collect { items ->
                    _collectionItems.value = items
                }
        }
    }
}
