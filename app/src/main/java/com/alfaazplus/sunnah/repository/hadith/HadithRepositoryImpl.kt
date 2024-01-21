package com.alfaazplus.sunnah.repository.hadith

import com.alfaazplus.sunnah.db.dao.HadithDao
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo
import com.alfaazplus.sunnah.ui.models.HadithWithTranslation

class HadithRepositoryImpl(private val dao: HadithDao) : HadithRepository {
    override suspend fun getCollection(collectionId: Int): CollectionWithInfo {
        return CollectionWithInfo(
            dao.getCollectionById(collectionId),
            dao.getCollectionInfoById("en", collectionId)
        )
    }

    override suspend fun getCollectionList(): List<CollectionWithInfo> {
        return dao.getCollectionList().map {
            CollectionWithInfo(it, dao.getCollectionInfoById("en", it.id))
        }
    }

    override suspend fun getBookList(collectionId: Int): List<BookWithInfo> {
        return dao.getBookList(collectionId).map {
            BookWithInfo(it, dao.getBookInfoById("en", it.id))
        }
    }

    override suspend fun getHadithCount(collectionId: Int, bookId: Int): Int {
        return dao.getHadithCount(collectionId, bookId)
    }

    override suspend fun getHadithList(collectionId: Int, bookId: Int): List<HadithWithTranslation> {
        return dao.getHadithList(collectionId, bookId).map {
            HadithWithTranslation(it, dao.getHadithTranslationByArURN(it.urn, "en"))
        }
    }

    override suspend fun getHadithByOrder(
        collectionId: Int,
        bookId: Int,
        orderInBook: Int
    ): HadithWithTranslation {
        val hadith = dao.getHadithByOrder(collectionId, bookId, orderInBook)
        val translation = dao.getHadithTranslationByArURN(hadith.urn, "en")

        return HadithWithTranslation(hadith, translation)
    }
}