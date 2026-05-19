package com.alfaazplus.sunnah.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.alfaazplus.sunnah.db.entities.v2.HadithReferenceEntity
import com.alfaazplus.sunnah.db.relations.BookWithTranslation
import com.alfaazplus.sunnah.db.relations.ChapterWithTranslation
import com.alfaazplus.sunnah.db.relations.CollectionWithTranslation
import com.alfaazplus.sunnah.db.relations.HadithNavigationItem
import com.alfaazplus.sunnah.db.relations.HadithWithContents
import kotlinx.coroutines.flow.Flow

@Dao
interface HadithDao2 {
    @Transaction
    @Query("SELECT * FROM collections ORDER BY sort_order")
    suspend fun getCollections(): List<CollectionWithTranslation>

    @Transaction
    @Query("SELECT * FROM collections ORDER BY sort_order")
    fun getCollectionsFlow(): Flow<List<CollectionWithTranslation>>

    @Transaction
    @Query(
        "SELECT * FROM collections WHERE id = :id"
    )
    suspend fun getCollectionById(id: String): CollectionWithTranslation?

    @Transaction
    @Query(
        """
            SELECT * FROM collections as c
            INNER JOIN books ON books.collection_id = c.id
            WHERE books.id = :bookId
            """
    )
    suspend fun getCollectionByBookId(bookId: String): CollectionWithTranslation?

    @Transaction
    @Query(
        """
            SELECT books.*, COUNT(hadiths.id) AS hadith_count
            FROM books
            LEFT JOIN hadiths
                ON hadiths.book_id = books.id
            WHERE books.collection_id = :collectionId
            GROUP BY books.id
            ORDER BY books.number + 0
        """
    )

    suspend fun getBooksForCollection(collectionId: String): List<BookWithTranslation>

    @Transaction
    @Query(
        """
            SELECT books.*, COUNT(hadiths.id) AS hadith_count
            FROM books
            LEFT JOIN hadiths
                ON hadiths.book_id = books.id
            WHERE books.id = :bookId
        """
    )

    suspend fun getBookById(bookId: String): BookWithTranslation?

    @Transaction
    @Query(
        """SELECT * FROM hadiths WHERE hadiths.id = :id """
    )
    suspend fun getHadithById(id: String): HadithWithContents?

    @Transaction
    @Query("SELECT * FROM hadiths WHERE book_id = :bookId ORDER BY urn")
    suspend fun getHadithsForBook(bookId: String): List<HadithWithContents>

    @Query(
        """
            SELECT
                hadiths.id AS hadithId,
                hadiths.book_id AS bookId,
                hadiths.number AS number
            FROM hadiths
            WHERE hadiths.book_id = :bookId
            ORDER BY hadiths.urn
        """
    )
    suspend fun getHadithNavigationItemsForBook(bookId: String): List<HadithNavigationItem>

    @Query("SELECT COUNT(*) FROM hadiths WHERE book_id = :bookId")
    suspend fun getHadithCountForBook(bookId: String): Int

    @Query(
        """
            SELECT COUNT(*)
            FROM hadiths
            WHERE book_id = :bookId
                AND urn < (
                    SELECT urn
                    FROM hadiths
                    WHERE id = :hadithId
                    LIMIT 1
                )
        """
    )
    suspend fun getHadithOffsetInBook(bookId: String, hadithId: String): Int

    @Transaction
    @Query("SELECT * FROM hadiths WHERE book_id = :bookId ORDER BY urn LIMIT :limit OFFSET :offset")
    suspend fun getHadithsForBookPage(
        bookId: String,
        limit: Int,
        offset: Int,
    ): List<HadithWithContents>

    @Transaction
    @Query("SELECT * FROM chapters WHERE book_id = :bookId")
    suspend fun getChaptersForBook(bookId: String): List<ChapterWithTranslation>

    @Transaction
    @Query("SELECT * FROM hadith_references WHERE hadith_id = :hadithId AND type = 'sunnahcom_reference' LIMIT 1")
    suspend fun getPrimaryReferenceForHadith(hadithId: String): HadithReferenceEntity?

    @Query("SELECT * FROM hadith_references WHERE hadith_id IN (:hadithIds) AND type = 'sunnahcom_reference'")
    suspend fun getPrimaryReferencesForHadiths(hadithIds: List<String>): List<HadithReferenceEntity>

    @Query("SELECT * FROM hadith_references WHERE hadith_id = :hadithId")
    suspend fun getReferencesForHadith(hadithId: String): List<HadithReferenceEntity>

    @Query(
        """
            SELECT COUNT(*) from hadith_narrators where hadith_id = :hadithId
        """
    )
    suspend fun countNarratorsForHadith(hadithId: String): Int

    @Query("SELECT hadith_id FROM hadith_narrators WHERE hadith_id IN (:hadithIds) GROUP BY hadith_id")
    suspend fun getHadithIdsWithNarrators(hadithIds: List<String>): List<String>

    @Query(
        """
            SELECT narrator_id FROM hadith_narrators
            WHERE hadith_id = :hadithId AND source = :source
            ORDER BY position DESC
        """
    )
    suspend fun getNarratorIdsForHadith(hadithId: String, source: String = "scholars"): List<Int>

    // ────────────────────────────────────────────────────────────────────────

    @Query(
        """
        SELECT h.id FROM hadiths AS h
        WHERE NOT EXISTS (
            SELECT 1 FROM hadith_grades AS g WHERE g.hadith_id = h.id
        )
        OR EXISTS (
            SELECT 1 FROM hadith_grades AS g
            WHERE g.hadith_id = h.id AND g.grade_id LIKE 'sahih%'
        )
        ORDER BY RANDOM()
        LIMIT 1
        """
    )
    suspend fun getRandomSahihHadithId(): String?
}
