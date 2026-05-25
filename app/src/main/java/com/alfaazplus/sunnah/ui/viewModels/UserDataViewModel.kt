package com.alfaazplus.sunnah.ui.viewModels

import android.app.Application
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.core.text.parseAsHtml
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.alfaazplus.sunnah.db.entities.userdata.v2.ReadHistory
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserBookmark
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserCollection
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserCollectionItem
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.repository.userdata.UserRepository
import com.alfaazplus.sunnah.ui.models.userdata.ReadHistoryNormalized
import com.alfaazplus.sunnah.ui.models.userdata.UserBookmarkNormalized
import com.alfaazplus.sunnah.ui.models.userdata.UserCollectionItemNormalized
import com.alfaazplus.sunnah.ui.models.userdata.UserDataUserItem
import com.alfaazplus.sunnah.ui.utils.StringUtils
import com.alfaazplus.sunnah.ui.utils.preferences.HadithTextOption
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.reader.ReaderItemsBuilder
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils
import com.alfaazplus.sunnah.ui.utils.text.textStyleForLang
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
    application: Application,
    val repo: UserRepository,
    private val hadithRepo: HadithRepository,
) : AndroidViewModel(application) {
    private val ctx get() = application.applicationContext
    private val bookmarkCache = mutableMapOf<String, StateFlow<Boolean>>()

    val allReadHistory: StateFlow<List<ReadHistoryNormalized>?> = repo.dao
        .observeReadHistory()
        .distinctUntilChanged()
        .map {
            normalizeReadHistory(it, true)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val recentReadHistory: StateFlow<List<ReadHistoryNormalized>?> = repo.dao
        .observeRecentReadHistory()
        .distinctUntilChanged()
        .map {
            normalizeReadHistory(it, false)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val userCollections: StateFlow<List<UserCollection>?> = repo.dao
        .observeUserCollections()
        .distinctUntilChanged()
        .map {
            normalizeCollections(it)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val allUserBookmarks: StateFlow<List<UserBookmarkNormalized>?> = repo.dao
        .observeUserBookmarks()
        .distinctUntilChanged()
        .map {
            normalizeBookmarks(it, true)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val recentUserBookmarks: StateFlow<List<UserBookmarkNormalized>?> = repo.dao
        .observeRecentUserBookmarks()
        .distinctUntilChanged()
        .map {
            normalizeBookmarks(it, false)
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
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

    private suspend fun normalizeCollections(items: List<UserCollection>): List<UserCollection> = withContext(Dispatchers.IO) {
        if (items.isEmpty()) return@withContext emptyList()

        val itemCounts = repo.dao
            .getUserCollectionItemCounts(
                items
                    .map { it.id }
                    .distinct())
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

    private suspend fun <RESULT : Any> normalizeItems(
        hadithIds: List<String>,
        withText: Boolean,
        transform: (Int, UserDataUserItem) -> RESULT,
    ): List<RESULT> = withContext(Dispatchers.IO) {
        if (hadithIds.isEmpty()) return@withContext emptyList()

        val textOption = ReaderPreferences.getHadithTextOption()
        val langCode = if (textOption == HadithTextOption.ONLY_ARABIC) "ar" else ReaderPreferences.getHadithTranslation()
        val serifFontStyle = ReaderPreferences.getIsSerifFontStyle()

        val hadiths = hadithRepo.getHadithsByIds(
            hadithIds,
            langCode,
        )

        val collectionsDeferred = async {
            hadithRepo.getCollectionsByIds(
                hadiths
                    .map { it.collectionId }
                    .distinct(),
                langCode,
            )
        }

        val booksDeferred = async {
            hadithRepo.getBooksByIds(
                hadiths
                    .map { it.bookId }
                    .distinct(),
                langCode,
            )
        }

        val hadithsMap = hadiths.associateBy { it.hadithId }

        val collectionNamesMap = collectionsDeferred
            .await()
            .associate { it.collection.id to (it.getTitle(langCode) ?: it.getTitle("ar")) }

        val bookNamesMap = booksDeferred
            .await()
            .associate { it.book.id to (it.getTitle(langCode) ?: it.getTitle("ar")) }


        val trTextStyle = textStyleForLang(
            langCode,
            isSerifFontStyle = serifFontStyle,
        )
        val trParagraphStyle = trTextStyle.toParagraphStyle();
        val trSpanStyle = trTextStyle.toSpanStyle();

        return@withContext hadithIds.mapIndexed { index, hadithId ->
            val hwc = hadithsMap[hadithId] ?: return@mapIndexed transform(
                index, UserDataUserItem(
                    hwc = null,
                    numbering = AnnotatedString("?"),
                    bookTitle = "?",
                    langCode = langCode,
                    translationText = null,
                )
            )

            val collectionName = collectionNamesMap[hwc.collectionId]

            val hadithText = if (withText) {
                val content = hwc.contents.firstOrNull { content -> content.lang == langCode }

                content?.let { content ->
                    val text = buildString {
                        val blocks = content.blocks

                        blocks.forEachIndexed { index, block ->
                            if (!block.text.isNullOrEmpty()) {
                                append(block.text.parseAsHtml())

                                if (index < blocks.lastIndex) {
                                    append(" ")
                                }
                            }
                        }
                    }.let { raw ->
                        if (raw.length > 250) raw.take(300) + StringUtils.ELLIPSIS else raw
                    }

                    buildAnnotatedString {
                        withStyle(trParagraphStyle) {
                            withStyle(trSpanStyle) {
                                append(text)
                            }
                        }
                    }
                } ?: TranslationUtils.getNoTranslationMessage(ctx, langCode)
            } else null

            transform(
                index, UserDataUserItem(
                    hwc = hwc,
                    numbering = ReaderItemsBuilder.buildNumbering(
                        collectionName,
                        hwc.hadith.number,
                        langCode,
                    ),
                    bookTitle = bookNamesMap[hwc.bookId] ?: "?",
                    langCode = langCode,
                    translationText = hadithText,
                )
            )
        }
    }
}
