package com.alfaazplus.sunnah.repository.hadith

import com.alfaazplus.sunnah.db.dao.ScholarsDao
import com.alfaazplus.sunnah.db.databases.HadithDatabase
import com.alfaazplus.sunnah.db.entities.scholars.Scholar
import com.alfaazplus.sunnah.db.relations.BookWithTranslation
import com.alfaazplus.sunnah.db.relations.CollectionWithTranslation
import com.alfaazplus.sunnah.db.relations.HadithWithContents
import com.alfaazplus.sunnah.ui.models.HadithOfTheDay
import kotlinx.coroutines.flow.Flow

class HadithRepository2(
    private val database: HadithDatabase,
    private val scholarsDao: ScholarsDao,
) {
    val dao get() = database.hadithDao
    val importDao get() = database.importDao

    fun getAllCollectionsFlow(): Flow<List<CollectionWithTranslation>> {
        return dao.getCollectionsFlow()
    }

    suspend fun getCollectionName(collectionId: String): String {
        val collectionTranslations = dao.getCollectionById(collectionId)?.translations ?: emptyList()

        val theC = sequenceOf("en", "ar").firstNotNullOfOrNull { langCode ->
            collectionTranslations.firstOrNull { it.lang == langCode }
        }

        return theC?.title ?: ""
    }

    suspend fun loadBooks(collectionId: String): List<BookWithTranslation> {
        val books = dao.getBooksForCollection(collectionId)

        return books.map {
            it.apply {

            }
        }
    }

    suspend fun loadSisterBooksFromBookId(bookId: String): List<BookWithTranslation> {
        val collectionId = dao.getCollectionByBookId(bookId)?.collection?.id ?: return emptyList()
        return dao.getBooksForCollection(collectionId)
    }

    // ─────────────────────────────────────────────────────────────────────────

    suspend fun getHotd(id: String): HadithOfTheDay? {
        val hwc = dao.getHadithById(id) ?: return null
        return HadithOfTheDay(
            hwc = hwc,
            collectionName = getCollectionName(hwc.hadith.collectionId),
        )
    }

    suspend fun getRandomSahihHadith(): HadithWithContents? {
        val hadithId = dao.getRandomSahihHadithId() ?: return null
        return dao.getHadithById(hadithId)
    }

    suspend fun getNarratorsOfHadith(hadithId: String): List<Scholar> {
        val ids = dao.getNarratorIdsForHadith(hadithId)

        if (ids.isEmpty()) return emptyList()

        val byId = scholarsDao.getScholars(ids).associateBy { it.id }

        return ids.mapNotNull { byId[it] }
    }

    suspend fun getDownloadedTranslations(langCodes: List<String>): List<String> {
        if (langCodes.isEmpty()) return emptyList()
        return importDao.getDownloadedTranslations(langCodes)
    }

    suspend fun deleteTranslationData(lang: String) {
        importDao.deleteTranslationData(lang)
    }
}
