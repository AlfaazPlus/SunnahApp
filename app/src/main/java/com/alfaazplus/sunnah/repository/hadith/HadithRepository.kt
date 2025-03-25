package com.alfaazplus.sunnah.repository.hadith

import com.alfaazplus.sunnah.db.models.scholars.Scholar
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo
import com.alfaazplus.sunnah.ui.models.HadithWithTranslation

interface HadithRepository {
    suspend fun getCollection(collectionId: Int): CollectionWithInfo
    suspend fun getCollectionList(): List<CollectionWithInfo>
    suspend fun getBookList(collectionId: Int): List<BookWithInfo>
    suspend fun getHadithCount(collectionId: Int, bookId: Int): Int
    suspend fun getHadithList(collectionId: Int, bookId: Int): List<HadithWithTranslation>
    suspend fun getHadithByOrder(collectionId: Int, bookId: Int, orderInBook: Int): HadithWithTranslation
    suspend fun deleteCollection(collectionId: Int)
    suspend fun getNarratorsOfHadith(urn: Int): List<Scholar>
}
