package com.alfaazplus.sunnah.repository.hadith

import androidx.compose.ui.graphics.Color
import androidx.paging.PagingData
import com.alfaazplus.sunnah.db.models.HadithOfTheDay
import com.alfaazplus.sunnah.db.models.scholars.Scholar
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.models.BooksSearchResult
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo
import com.alfaazplus.sunnah.ui.models.HadithSearchResult
import com.alfaazplus.sunnah.ui.models.HadithWithTranslation
import kotlinx.coroutines.flow.Flow

interface HadithRepository {
    suspend fun getCollection(collectionId: Int): CollectionWithInfo
    suspend fun getCollectionList(): List<CollectionWithInfo>
    suspend fun getBookList(collectionId: Int): List<BookWithInfo>
    suspend fun getHadithCount(collectionId: Int, bookId: Int): Int
    suspend fun getHadithList(collectionId: Int, bookId: Int): List<HadithWithTranslation>
    suspend fun getHadithByOrder(collectionId: Int, bookId: Int, orderInBook: Int): HadithWithTranslation
    suspend fun deleteCollection(collectionId: Int)
    suspend fun getNarratorsOfHadith(urn: Int): List<Scholar>
    suspend fun searchHadiths(query: String, collectionIds: List<Int>?, color: Color): Flow<PagingData<HadithSearchResult>>
    suspend fun searchBooks(query: String, collectionIds: List<Int>?): Flow<PagingData<BooksSearchResult>>
    suspend fun searchScholars(query: String): Flow<PagingData<Scholar>>
    suspend fun getHotd(urn: String): HadithOfTheDay?
    suspend fun getNewHotd(): HadithOfTheDay?
}
