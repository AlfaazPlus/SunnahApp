package com.alfaazplus.sunnah.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import com.alfaazplus.sunnah.db.entities.v2.HadithReferenceEntity
import com.alfaazplus.sunnah.db.relations.ChapterWithTranslation
import com.alfaazplus.sunnah.db.relations.CollectionWithTranslations
import com.alfaazplus.sunnah.db.relations.HadithWithContents
import kotlinx.coroutines.flow.Flow

@Dao
interface HadithDao2 {
    @Query("SELECT * FROM collections ORDER BY sort_order")
    suspend fun getCollections(): List<CollectionWithTranslations>

    @Query("SELECT * FROM collections ORDER BY sort_order")
    fun getCollectionsFlow(): Flow<List<CollectionWithTranslations>>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        "SELECT * FROM collections WHERE id = :id"
    )
    suspend fun getCollectionById(id: String): CollectionWithTranslations?

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
            SELECT * FROM collections as c
            INNER JOIN books ON books.collection_id = c.id
            WHERE books.id = :bookId
            """
    )
    suspend fun getCollectionByBookId(bookId: String): CollectionWithTranslations?

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """SELECT * FROM hadiths WHERE hadiths.id = :id """
    )
    suspend fun getHadithById(id: String): HadithWithContents?

    @Query("SELECT * FROM hadiths WHERE book_id = :bookId ORDER BY rowid")
    suspend fun getHadithsForBook(bookId: String): List<HadithWithContents>

    @Query("SELECT * FROM chapters WHERE book_id = :bookId")
    suspend fun getChaptersForBook(bookId: String): List<ChapterWithTranslation>

    @Query(
        """
            SELECT COUNT(*) from hadith_narrators where hadith_id = :hadithId
        """
    )
    suspend fun countNarratorsForHadith(hadithId: String): Int

    // ────────────────────────────────────────────────────────────────────────

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT * FROM hadiths
        WHERE id = (
            SELECT hadith_id FROM hadith_grades
            WHERE grade_id LIKE 'sahih%'
            ORDER BY RANDOM()
            LIMIT 1
        )
        """
    )
    suspend fun getRandomHadith(): HadithWithContents?
}
