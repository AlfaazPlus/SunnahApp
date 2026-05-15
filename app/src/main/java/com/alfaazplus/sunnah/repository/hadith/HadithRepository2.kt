package com.alfaazplus.sunnah.repository.hadith

import com.alfaazplus.sunnah.db.dao.ScholarsDao
import com.alfaazplus.sunnah.db.databases.HadithDatabase
import com.alfaazplus.sunnah.db.relations.ChapterWithTranslation
import com.alfaazplus.sunnah.db.relations.CollectionWithTranslations
import com.alfaazplus.sunnah.db.relations.HadithWithContents
import com.alfaazplus.sunnah.ui.models.HadithOfTheDay
import kotlinx.coroutines.flow.Flow

class HadithRepository2(
    private val database: HadithDatabase,
    private val scholarsDao: ScholarsDao,
) {
    val dao get() = database.hadithDao

    fun getAllCollectionsFlow(): Flow<List<CollectionWithTranslations>> {
        return dao.getCollectionsFlow()
    }

    suspend fun getCollectionName(collectionId: String): String {
        val collectionTranslations = dao.getCollectionById(collectionId)?.translations ?: emptyList()

        val theC = sequenceOf("en", "ar").firstNotNullOfOrNull { langCode ->
            collectionTranslations.firstOrNull { it.lang == langCode }
        }

        return theC?.title ?: ""
    }

    suspend fun getHotd(id: String): HadithOfTheDay? {
        val hwc = dao.getHadithById(id) ?: return null
        return HadithOfTheDay(
            hwc = hwc,
            collectionName = getCollectionName(hwc.hadith.collectionId),
        )
    }

    // ─────────────────────────────────────────────────────────────────────────

    suspend fun getRandomHadith(): HadithWithContents? {
        return dao.getRandomHadith()
    }
}
