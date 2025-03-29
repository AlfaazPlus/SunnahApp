package com.alfaazplus.sunnah.repository.hadith

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.core.text.parseAsHtml
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.db.dao.HadithDao
import com.alfaazplus.sunnah.db.dao.ScholarsDao
import com.alfaazplus.sunnah.db.models.HadithOfTheDay
import com.alfaazplus.sunnah.db.models.scholars.Scholar
import com.alfaazplus.sunnah.ui.misc.EmptyPagingSource
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.models.BooksSearchResult
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo
import com.alfaazplus.sunnah.ui.models.HadithSearchResult
import com.alfaazplus.sunnah.ui.models.HadithWithTranslation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HadithRepositoryImpl(
    private val dao: HadithDao,
    private val scholarsDao: ScholarsDao,
) : HadithRepository {
    override suspend fun getCollection(collectionId: Int): CollectionWithInfo {
        return CollectionWithInfo(
            dao.getCollectionById(collectionId), dao.getCollectionInfoById("en", collectionId)
        )
    }

    override suspend fun getCollectionList(): List<CollectionWithInfo> {
        return dao
            .getCollectionList()
            .map {
                CollectionWithInfo(it, dao.getCollectionInfoById("en", it.id))
            }
    }

    override suspend fun getBookList(collectionId: Int): List<BookWithInfo> {
        return dao
            .getBookList(collectionId)
            .map {
                BookWithInfo(it, dao.getBookInfoById("en", collectionId, it.id))
            }
    }

    override suspend fun getHadithCount(collectionId: Int, bookId: Int): Int {
        return dao.getHadithCount(collectionId, bookId)
    }

    override suspend fun getHadithList(collectionId: Int, bookId: Int): List<HadithWithTranslation> {
        return dao
            .getHadithList(collectionId, bookId)
            .map { //            val chapter = it.chapterId?.let { chapterId -> dao.getChapterWithInfoById(chapterId) }
                HadithWithTranslation(
                    it,
                    dao.getHadithTranslationByArURN(it.urn, "en"), //                chapter,
                )
            }
    }

    override suspend fun getHadithByOrder(
        collectionId: Int,
        bookId: Int,
        orderInBook: Int,
    ): HadithWithTranslation {
        val hadith = dao.getHadithByOrder(collectionId, bookId, orderInBook)
        val translation = dao.getHadithTranslationByArURN(hadith.urn, "en")

        return HadithWithTranslation(hadith, translation)
    }

    override suspend fun deleteCollection(collectionId: Int) {
        dao.deleteCollection(collectionId)
    }


    override suspend fun getNarratorsOfHadith(urn: Int): List<Scholar> {
        val narratorIdsStr = dao.getNarratorIds(urn)
        val narratorIds = narratorIdsStr
            .split(",")
            .map { it.toInt() }

        return scholarsDao.getScholars(narratorIds)
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

    override suspend fun getScholarInfo(scholarId: Int): Scholar? {
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

    override suspend fun searchHadiths(query: String, collectionIds: List<Int>?, color: Color): Flow<PagingData<HadithSearchResult>> {
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
                    dao.searchHadiths(query, collectionIds, "en")
                }
            },
        ).flow.map { pagingData ->
            pagingData.map { searchResult ->
                val baseText = searchResult.translation.hadithText
                    .parseAsHtml()
                    .toString()
                val padding = 200
                val highlightStart = baseText.indexOf(query, ignoreCase = true)
                val highlightEnd = highlightStart + query.length

                // Ensure the context range is within bounds
                val preContextStart = (highlightStart - padding).coerceAtLeast(0)
                val postContextEnd = (highlightEnd + padding).coerceAtMost(baseText.length)

                val preEllipsis = preContextStart > 0
                val postEllipsis = postContextEnd < baseText.length

                searchResult.translationText = buildAnnotatedString {
                    if (preEllipsis) append("…")
                    append(baseText.subSequence(preContextStart, highlightStart))

                    // Highlighted portion
                    addStyle(
                        SpanStyle(
                            color = color,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                        ),
                        length,
                        length + query.length,
                    )
                    append(baseText.subSequence(highlightStart, highlightEnd))

                    append(baseText.subSequence(highlightEnd, postContextEnd))
                    if (postEllipsis) append("…")
                }

                searchResult
            }
        }
    }

    override suspend fun searchBooks(query: String, collectionIds: List<Int>?): Flow<PagingData<BooksSearchResult>> {
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
                    dao.searchBooks(query, collectionIds, "en")
                }
            },
        ).flow
    }

    override suspend fun searchScholars(query: String): Flow<PagingData<Scholar>> {
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

    override suspend fun getHotd(urn: String): HadithOfTheDay? {
        return dao
            .getHotd(urn, "en")
            ?.apply {
                this.collectionName = dao.getCollectionInfoById("en", this.hadith.collectionId).name
            }
    }

    override suspend fun getNewHotd(): HadithOfTheDay? {
        return dao
            .getNewHotd(300, "en")
            ?.apply {
                this.collectionName = dao.getCollectionInfoById("en", this.hadith.collectionId).name
            }
    }
}