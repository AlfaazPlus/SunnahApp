package com.alfaazplus.sunnah.repository.hadith

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.api.JsonHelper
import com.alfaazplus.sunnah.db.dao.ScholarsDao
import com.alfaazplus.sunnah.db.databases.HadithDatabase
import com.alfaazplus.sunnah.db.entities.scholars.Scholar
import com.alfaazplus.sunnah.db.entities.v2.ChapterEntity
import com.alfaazplus.sunnah.db.entities.v2.CollectionEntity
import com.alfaazplus.sunnah.db.entities.v2.CollectionTranslationEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithBlock
import com.alfaazplus.sunnah.db.entities.v2.HadithBlockType
import com.alfaazplus.sunnah.db.entities.v2.HadithEntity
import com.alfaazplus.sunnah.db.relations.BookWithHadithCount
import com.alfaazplus.sunnah.db.relations.BookWithTranslation
import com.alfaazplus.sunnah.db.relations.ChapterWithTranslation
import com.alfaazplus.sunnah.db.relations.CollectionWithTranslation
import com.alfaazplus.sunnah.db.relations.HadithWithContents
import com.alfaazplus.sunnah.ui.misc.EmptyPagingSource
import com.alfaazplus.sunnah.ui.models.BookSearchQuickResult
import com.alfaazplus.sunnah.ui.models.BooksSearchResult
import com.alfaazplus.sunnah.ui.models.HadithOfTheDay
import com.alfaazplus.sunnah.ui.models.HadithSearchQuickResult
import com.alfaazplus.sunnah.ui.models.HadithSearchResult
import com.alfaazplus.sunnah.ui.models.HadithSearchRow
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class HadithRepository(
    private val database: HadithDatabase,
    private val scholarsDao: ScholarsDao,
) {
    val dao get() = database.hadithDao
    val importDao get() = database.importDao

    suspend fun isAnyCollectionDownloaded(): Boolean {
        return dao
            .getCollections()
            .isNotEmpty()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllCollectionsFlow(): Flow<List<CollectionWithTranslation>> {
        return ReaderPreferences
            .hadithTranslationFlow()
            .flatMapLatest { langCode ->
                combine(
                    dao.getCollectionsFlow(),
                    dao.getCollectionTranslationsFlow(TranslationUtils.metadataLangCodes(langCode)),
                ) { collections, translations ->
                    collections.withCollectionTranslations(translations.groupBy { it.collectionId })
                }
            }
    }

    suspend fun getCollectionName(collectionId: String, langCode: String): String {
        return getCollectionById(collectionId, langCode)
            ?.getTitle(langCode)
            .orEmpty()
    }

    suspend fun getCollectionById(id: String, langCode: String): CollectionWithTranslation? {
        return dao
            .getCollectionById(id)
            ?.withCollectionTranslations(langCode)
    }

    suspend fun getCollectionsByIds(collectionIds: List<String>, langCode: String): List<CollectionWithTranslation> {
        if (collectionIds.isEmpty()) return emptyList()

        return dao
            .getCollectionsByIds(collectionIds)
            .withCollectionTranslations(langCode)
    }

    suspend fun getCollectionByBookId(bookId: String, langCode: String): CollectionWithTranslation? {
        return dao
            .getCollectionByBookId(bookId)
            ?.withCollectionTranslations(langCode)
    }

    suspend fun loadBooks(collectionId: String, langCode: String): List<BookWithTranslation> {
        return dao
            .getBooksForCollection(collectionId)
            .withBookTranslations(langCode)
    }

    suspend fun getBookById(bookId: String, langCode: String): BookWithTranslation? {
        return dao
            .getBookById(bookId)
            ?.withBookTranslations(langCode)
    }

    suspend fun getBooksByIds(bookIds: List<String>, langCode: String): List<BookWithTranslation> {
        if (bookIds.isEmpty()) return emptyList()
        return dao
            .getBooksByIds(bookIds)
            .withBookTranslations(langCode)
    }

    suspend fun loadSisterBooksFromBookId(bookId: String, langCode: String): List<BookWithTranslation> {
        val collectionId = dao.getCollectionByBookId(bookId)?.id ?: return emptyList()
        return loadBooks(collectionId, langCode)
    }

    // ─────────────────────────────────────────────────────────────────────────

    suspend fun getHotd(id: String, langCode: String): HadithOfTheDay? {
        val hwc = getHadithById(id, langCode) ?: return null
        return HadithOfTheDay(
            hwc = hwc,
            collectionName = getCollectionName(hwc.hadith.collectionId, langCode),
        )
    }

    suspend fun getRandomSahihHadith(): HadithWithContents? {
        val hadithId = dao.getRandomSahihHadithId() ?: return null
        val langCode = ReaderPreferences.getHadithTranslation()
        return getHadithById(hadithId, langCode)
    }

    suspend fun getHadithById(id: String, langCode: String): HadithWithContents? {
        return dao
            .getHadithById(id)
            ?.withContents(langCode)
    }

    suspend fun getHadithsByIds(ids: List<String>, langCode: String): List<HadithWithContents> {
        if (ids.isEmpty()) return emptyList()

        return dao
            .getHadithsByIds(ids)
            .withContents(langCode)
    }

    suspend fun getHadithsForBookPage(
        bookId: String,
        limit: Int,
        offset: Int,
        langCode: String,
    ): List<HadithWithContents> {
        return dao
            .getHadithsForBookPage(bookId, limit, offset)
            .withContents(langCode)
    }

    suspend fun getChaptersForBook(bookId: String, langCode: String): List<ChapterWithTranslation> {
        return dao
            .getChaptersForBook(bookId)
            .withChapterTranslations(langCode)
    }

    suspend fun getNarratorsOfHadith(hadithId: String): List<Scholar> {
        val ids = dao.getNarratorIdsForHadith(hadithId)

        if (ids.isEmpty()) return emptyList()

        val byId = scholarsDao
            .getScholars(ids)
            .associateBy { it.id }

        return ids.mapNotNull { byId[it] }
    }

    suspend fun getDownloadedTranslations(langCodes: List<String>): List<String> {
        if (langCodes.isEmpty()) return emptyList()
        return importDao.getDownloadedTranslations(langCodes)
    }

    suspend fun deleteTranslationData(lang: String) {
        importDao.deleteTranslationData(lang)
    }

    fun searchHadiths(
        query: String,
        collectionIds: List<String>?,
        color: Color,
        langCode: String,
    ): Flow<PagingData<HadithSearchResult>> {
        Logger.d("Searching for hadiths with query: $query", "CollectionIds: $collectionIds")

        return Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                enablePlaceholders = true,
                prefetchDistance = 3,
            ),
            pagingSourceFactory = {
                if (query.isBlank()) {
                    EmptyPagingSource()
                } else {
                    dao.searchHadiths(query, collectionIds, langCode)
                }
            },
        ).flow.map { pagingData ->
            pagingData.map { row -> row.toSearchResult(query, color) }
        }
    }

    fun searchBooks(
        query: String,
        collectionIds: List<String>?,
        langCode: String,
    ): Flow<PagingData<BooksSearchResult>> {
        Logger.d("Searching for books with query: $query", "CollectionIds: $collectionIds")

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 3,
            ),
            pagingSourceFactory = {
                if (query.isBlank()) {
                    EmptyPagingSource()
                } else {
                    dao.searchBooks(query, collectionIds, langCode)
                }
            },
        ).flow
    }

    fun searchScholars(query: String): Flow<PagingData<Scholar>> {
        Logger.d("Searching for scholars with query: $query")

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 3,
            ),
            pagingSourceFactory = {
                if (query.isBlank()) {
                    EmptyPagingSource()
                } else {
                    scholarsDao.searchScholars(query)
                }
            },
        ).flow
    }

    suspend fun getQuickHadithSearchResults(
        query: String,
        langCode: String,
    ): List<HadithSearchQuickResult> {
        val parts = query.split(":")

        if (parts.size > 1) {
            val bookNumber = parseSearchNumber(parts[0]) ?: return emptyList()
            val hadithOrder = parseSearchNumber(parts[1]) ?: return emptyList()
            val offset = (hadithOrder.second - 1).coerceAtLeast(0)

            return dao.searchQuickHadithByBookOrder(bookNumber.first, offset, langCode)
        }

        val hadithNumber = parseSearchNumber(query)?.first ?: return emptyList()
        return dao.searchQuickHadithsByHadithNumber(hadithNumber, langCode)
    }

    suspend fun getQuickBookSearchResults(
        query: String,
        langCode: String,
    ): List<BookSearchQuickResult> {
        val bookNumber = parseSearchNumber(query)?.first ?: return emptyList()
        return dao.searchQuickBooks(bookNumber, langCode)
    }

    suspend fun getScholarInfo(scholarId: Int): Scholar? {
        Logger.d("Fetching scholar info for ID: $scholarId")

        return scholarsDao
            .getScholarById(scholarId)
            ?.apply {
                this.fatherName = fatherName?.fetchScholarNames()
                this.motherName = motherName?.fetchScholarNames()
                this.spouses = spouses?.fetchScholarNames()
                this.children = children?.fetchScholarNames()
                this.siblings = siblings?.fetchScholarNames()
                this.teachers = teachers?.fetchScholarNames()
                this.students = students?.fetchScholarNames()
            }
    }

    private suspend fun String.fetchScholarNames(): String {
        val bullet = "\u2022"
        var names = mutableSetOf<String>()
        val ids = mutableSetOf<Int>()

        this
            .split(",")
            .forEach { childId ->
                childId
                    .toIntOrNull()
                    ?.let { id ->
                        ids.add(id)
                    } ?: names.add(childId)
            }

        if (ids.isNotEmpty()) {
            val scholars = scholarsDao.getScholarsByIds(ids.toList())
            names = LinkedHashSet<String>().apply {
                addAll(scholars.map { it.shortName ?: "" })
                addAll(names)
            }
        }

        if (names.size <= 1) {
            return names.firstOrNull() ?: ""
        }

        return names.joinToString(separator = "\n") { name ->
            "$bullet\t\t$name"
        }
    }

    private fun HadithSearchRow.toSearchResult(query: String, highlightColor: Color): HadithSearchResult {
        val plainText = blocksJson.toPlainSearchText()
        return HadithSearchResult(
            hadithId = hadithId,
            bookId = bookId,
            collectionId = collectionId,
            hadithNumber = hadithNumber,
            collectionName = collectionName,
            plainText = plainText,
        ).apply {
            translationText = plainText.toHighlightedText(query, highlightColor)
        }
    }

    private fun String.toPlainSearchText(): String = try {
        JsonHelper.json
            .decodeFromString<List<HadithBlock>>(this)
            .asSequence()
            .filter {
                it.type == HadithBlockType.MATN || it.type == HadithBlockType.TRANSLATION || it.type == HadithBlockType.NARRATOR
            }
            .mapNotNull { it.text?.trim() }
            .filter { it.isNotEmpty() }
            .joinToString(" ")
    } catch (_: Exception) {
        this
    }

    private fun String.toHighlightedText(query: String, color: Color): AnnotatedString {
        if (isBlank()) return buildAnnotatedString { }

        val highlightStart = indexOf(query, ignoreCase = true)
        if (highlightStart == -1) {
            return buildAnnotatedString {
                append(take(400))
                if (length > 400) append("…")
            }
        }

        val highlightEnd = highlightStart + query.length
        val padding = 200
        val preContextStart = (highlightStart - padding).coerceAtLeast(0)
        val postContextEnd = (highlightEnd + padding).coerceAtMost(length)

        return buildAnnotatedString {
            if (preContextStart > 0) append("…")
            append(subSequence(preContextStart, highlightStart))
            addStyle(
                SpanStyle(color = color, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic),
                length,
                length + query.length,
            )
            append(subSequence(highlightStart, highlightEnd))
            append(subSequence(highlightEnd, postContextEnd))
            if (postContextEnd < length) append("…")
        }
    }

    private suspend fun CollectionEntity.withCollectionTranslations(langCode: String): CollectionWithTranslation {
        return listOf(this)
            .withCollectionTranslations(langCode)
            .first()
    }

    private suspend fun List<CollectionEntity>.withCollectionTranslations(langCode: String): List<CollectionWithTranslation> {
        if (isEmpty()) return emptyList()

        val translations = dao
            .getCollectionTranslations(
                collectionIds = map { it.id }.distinct(),
                langCodes = TranslationUtils.metadataLangCodes(langCode),
            )
            .groupBy { it.collectionId }

        return withCollectionTranslations(translations)
    }

    private fun List<CollectionEntity>.withCollectionTranslations(
        translationsByCollectionId: Map<String, List<CollectionTranslationEntity>>,
    ): List<CollectionWithTranslation> {
        return map { collection ->
            CollectionWithTranslation(
                collection = collection,
                translations = translationsByCollectionId[collection.id].orEmpty(),
            )
        }
    }

    private suspend fun BookWithHadithCount.withBookTranslations(langCode: String): BookWithTranslation {
        return listOf(this)
            .withBookTranslations(langCode)
            .first()
    }

    private suspend fun List<BookWithHadithCount>.withBookTranslations(langCode: String): List<BookWithTranslation> {
        if (isEmpty()) return emptyList()

        val translations = dao
            .getBookTranslations(
                bookIds = map { it.book.id }.distinct(),
                langCodes = TranslationUtils.metadataLangCodes(langCode),
            )
            .groupBy { it.bookId }

        return map { item ->
            BookWithTranslation(
                book = item.book,
                translations = translations[item.book.id].orEmpty(),
                hadithCount = item.hadithCount,
            )
        }
    }

    private suspend fun HadithEntity.withContents(langCode: String): HadithWithContents {
        return listOf(this)
            .withContents(langCode)
            .first()
    }

    private suspend fun List<HadithEntity>.withContents(langCode: String): List<HadithWithContents> {
        if (isEmpty()) return emptyList()

        val hadithIds = map { it.id }.distinct()
        val contents = dao
            .getHadithContents(
                hadithIds = hadithIds,
                langCodes = TranslationUtils.contentLangCodes(langCode),
            )
            .groupBy { it.hadithId }

        val grades = dao
            .getHadithGrades(
                hadithIds = hadithIds,
                langCodes = TranslationUtils.gradeLangCodes(langCode),
            )
            .groupBy { it.hadithId }

        return map { hadith ->
            HadithWithContents(
                hadith = hadith,
                contents = contents[hadith.id].orEmpty(),
                grades = grades[hadith.id].orEmpty(),
            )
        }
    }

    private suspend fun List<ChapterEntity>.withChapterTranslations(langCode: String): List<ChapterWithTranslation> {
        if (isEmpty()) return emptyList()

        val translations = dao
            .getChapterTranslations(
                chapterIds = map { it.id }.distinct(),
                langCodes = TranslationUtils.metadataLangCodes(langCode),
            )
            .groupBy { it.chapterId }

        return map { chapter ->
            ChapterWithTranslation(
                chapter = chapter,
                translations = translations[chapter.id].orEmpty(),
            )
        }
    }
}

private fun parseSearchNumber(query: String): Pair<String, Int>? {
    val cleaned = query
        .lowercase()
        .replace("\\s+".toRegex(), "")
    val match = Regex("(\\d+)([a-zA-Z]?)").find(cleaned) ?: return null
    val number = match.groupValues[1]
    val suffix = match.groupValues
        .getOrNull(2)
        ?.firstOrNull()
    val numString = if (suffix != null) "$number$suffix" else number
    val numInt = number.toIntOrNull() ?: return null
    return numString to numInt
}
