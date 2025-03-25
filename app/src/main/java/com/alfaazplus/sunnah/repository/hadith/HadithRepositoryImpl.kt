package com.alfaazplus.sunnah.repository.hadith

import com.alfaazplus.sunnah.db.dao.HadithDao
import com.alfaazplus.sunnah.db.dao.ScholarsDao
import com.alfaazplus.sunnah.db.models.scholars.Scholar
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo
import com.alfaazplus.sunnah.ui.models.HadithWithTranslation

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
        return dao.getCollectionList().map {
            CollectionWithInfo(it, dao.getCollectionInfoById("en", it.id))
        }
    }

    override suspend fun getBookList(collectionId: Int): List<BookWithInfo> {
        return dao.getBookList(collectionId).map {
            BookWithInfo(it, dao.getBookInfoById("en", collectionId, it.id))
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
        val narratorIds = narratorIdsStr.split(",").map { it.toInt() }

        return scholarsDao.getScholars(narratorIds)
    }
}