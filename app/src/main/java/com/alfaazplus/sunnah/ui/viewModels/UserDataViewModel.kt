package com.alfaazplus.sunnah.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alfaazplus.sunnah.db.entities.userdata.v2.ReadHistory
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserBookmark
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserCollection
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserCollectionItem
import com.alfaazplus.sunnah.repository.userdata.UserRepository
import com.alfaazplus.sunnah.ui.models.userdata.ReadHistoryNormalized
import com.alfaazplus.sunnah.ui.models.userdata.UserBookmarkNormalized
import com.alfaazplus.sunnah.ui.models.userdata.UserCollectionItemNormalized
import com.alfaazplus.sunnah.ui.models.userdata.UserDataUserItem
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserDataViewModel @Inject constructor(
    val repo: UserRepository,
) : ViewModel() {
    private val bookmarkCache = mutableMapOf<String, StateFlow<Boolean>>()

    val allReadHistory: StateFlow<List<ReadHistoryNormalized>> = repo.dao
        .observeReadHistory()
        .distinctUntilChanged()
        .map {
            normalizeReadHistory(it, true)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val recentReadHistory: StateFlow<List<ReadHistoryNormalized>> = repo.dao
        .observeRecentReadHistory()
        .distinctUntilChanged()
        .map {
            normalizeReadHistory(it, false)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val userCollections: StateFlow<List<UserCollection>> = repo.dao
        .observeUserCollections()
        .distinctUntilChanged()
        .map {
            normalizeCollections(it)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val allUserBookmarks: StateFlow<List<UserBookmarkNormalized>> = repo.dao
        .observeUserBookmarks()
        .distinctUntilChanged()
        .map {
            normalizeBookmarks(it, true)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val recentUserBookmarks: StateFlow<List<UserBookmarkNormalized>> = repo.dao
        .observeRecentUserBookmarks()
        .distinctUntilChanged()
        .map {
            normalizeBookmarks(it, false)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    private val _collectionItems = MutableStateFlow<List<UserCollectionItemNormalized>>(emptyList())
    val collectionItems: StateFlow<List<UserCollectionItemNormalized>> = _collectionItems

    fun isBookmarked(hadithId: String): StateFlow<Boolean> {
        return bookmarkCache.getOrPut(hadithId) {
            repo
                .observeUserBookmark(hadithId)
                .map { it != null }
                .stateIn(
                    viewModelScope,
                    started = SharingStarted.Eagerly,
                    initialValue = false,
                )
        }
    }

    fun loadCollectionItems(collectionId: Long) {
        viewModelScope.launch {
            repo.dao
                .observeUserCollectionItems(collectionId)
                .distinctUntilChanged()
                .collect { items ->
                    _collectionItems.value = normalizeCollectionItems(items)
                }
        }
    }

    private suspend fun normalizeCollections(items: List<UserCollection>): List<UserCollection> =
        withContext(Dispatchers.IO) {
            if (items.isEmpty()) return@withContext emptyList()

            val itemCounts = repo.dao
                .getUserCollectionItemCounts(
                    items
                        .map { it.id }
                        .distinct()
                )
                .associateBy { it.collectionId }

            items.map {
                it.apply {
                    itemsCount = itemCounts[it.id]?.itemsCount ?: 0
                }
            }
        }

    private suspend fun normalizeReadHistory(items: List<ReadHistory>, withText: Boolean): List<ReadHistoryNormalized> {
        return normalizeItems(
            items
                .map { it.hadithId }
                .distinct(),
            withText,
        ) { index, ui ->
            ReadHistoryNormalized(
                items[index], ui
            )
        }
    }

    private suspend fun normalizeBookmarks(items: List<UserBookmark>, withText: Boolean): List<UserBookmarkNormalized> {
        return normalizeItems(
            items
                .map { it.hadithId }
                .distinct(),
            withText,
        ) { index, ui ->
            UserBookmarkNormalized(
                items[index], ui
            )
        }
    }

    private suspend fun normalizeCollectionItems(items: List<UserCollectionItem>): List<UserCollectionItemNormalized> {
        return normalizeItems(
            items
                .map { it.hadithId }
                .distinct(),
            true,
        ) { index, ui ->
            UserCollectionItemNormalized(
                items[index], ui
            )
        }
    }

    private suspend fun <R : Any> normalizeItems(
        hadithIds: List<String>,
        withText: Boolean,
        transform: (Int, UserDataUserItem) -> R,
    ): List<R> = withContext(Dispatchers.IO) {
        if (hadithIds.isEmpty()) return@withContext emptyList()

        val translationId = ReaderPreferences.getHadithTranslation()
        val langCode = TranslationUtils.langCodeFromId(translationId)

        val hadiths = repo.hadithDao.getHadithsByIds(
            hadithIds,
        )

        val collectionsDeferred = async {
            repo.hadithDao.getCollectionsByIds(
                hadiths
                    .map { it.collectionId }
                    .distinct(),
            )
        }

        val booksDeferred = async {
            repo.hadithDao.getBooksByIds(
                hadiths
                    .map { it.bookId }
                    .distinct(),
            )
        }

        val hadithsMap = hadiths.associateBy { it.hadithId }

        val collectionNamesMap = collectionsDeferred
            .await()
            .associate { it.collection.id to it.getTitle(langCode) }

        val bookNamesMap = booksDeferred
            .await()
            .associate { it.book.id to it.getTitle(langCode) }

        val primaryReferences = repo.hadithDao
            .getPrimaryReferencesForHadiths(hadithIds)
            .associateBy { it.hadithId }

        return@withContext hadithIds.mapIndexed { index, hadithId ->
            val hwc = hadithsMap[hadithId] ?: return@mapIndexed transform(
                index, UserDataUserItem(
                    hwc = null,
                    visibleNumbering = "?",
                    bookTitle = "?",
                    translationText = null,
                )
            )

            val collectionName = collectionNamesMap[hwc.collectionId]

            val hadithText = if (withText) {
                val content = hwc.contents.firstOrNull { content -> content.lang == translationId }
                content?.toPlainText()
            } else null

            transform(
                index, UserDataUserItem(
                    hwc = hwc,
                    visibleNumbering = primaryReferences[hwc.hadithId]?.value ?: "${collectionName}: ${hwc.hadith.number}",
                    bookTitle = bookNamesMap[hwc.bookId] ?: "?",
                    translationText = hadithText,
                )
            )
        }
    }
}
