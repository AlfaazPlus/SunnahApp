package com.alfaazplus.sunnah.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.alfaazplus.sunnah.db.entities.migration.HadithIdUrnLookup
import com.alfaazplus.sunnah.db.entities.v2.BookTranslationEntity
import com.alfaazplus.sunnah.db.entities.v2.ChapterEntity
import com.alfaazplus.sunnah.db.entities.v2.ChapterTranslationEntity
import com.alfaazplus.sunnah.db.entities.v2.CollectionEntity
import com.alfaazplus.sunnah.db.entities.v2.CollectionTranslationEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithContentEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithGradeEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithReferenceEntity
import com.alfaazplus.sunnah.db.relations.BookWithHadithCount
import com.alfaazplus.sunnah.db.relations.HadithNavigationItem
import com.alfaazplus.sunnah.ui.models.BookSearchQuickResult
import com.alfaazplus.sunnah.ui.models.BooksSearchResult
import com.alfaazplus.sunnah.ui.models.HadithSearchQuickResult
import com.alfaazplus.sunnah.ui.models.HadithSearchRow
import kotlinx.coroutines.flow.Flow

@Dao
interface HadithDao {
    @Query("SELECT * FROM collections ORDER BY sort_order")
    suspend fun getCollections(): List<CollectionEntity>

    @Query("SELECT * FROM collections ORDER BY sort_order")
    fun getCollectionsFlow(): Flow<List<CollectionEntity>>

    @Query(
        "SELECT * FROM collections WHERE id = :id"
    )
    suspend fun getCollectionById(id: String): CollectionEntity?

    @Query(
        "SELECT * FROM collections WHERE id IN (:collectionIds)"
    )
    suspend fun getCollectionsByIds(collectionIds: List<String>): List<CollectionEntity>

    @Query(
        """
            SELECT c.* FROM collections as c
            INNER JOIN books ON books.collection_id = c.id
            WHERE books.id = :bookId
            """
    )
    suspend fun getCollectionByBookId(bookId: String): CollectionEntity?

    @Query("SELECT * FROM collection_translations WHERE collection_id IN (:collectionIds) AND lang IN (:langCodes)")
    suspend fun getCollectionTranslations(
        collectionIds: List<String>,
        langCodes: List<String>,
    ): List<CollectionTranslationEntity>

    @Query("SELECT * FROM collection_translations WHERE lang IN (:langCodes)")
    fun getCollectionTranslationsFlow(langCodes: List<String>): Flow<List<CollectionTranslationEntity>>

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
    suspend fun getBooksForCollection(collectionId: String): List<BookWithHadithCount>

    @Query(
        """
            SELECT books.*, COUNT(hadiths.id) AS hadith_count
            FROM books
            LEFT JOIN hadiths
                ON hadiths.book_id = books.id
            WHERE books.id = :bookId
        """
    )
    suspend fun getBookById(bookId: String): BookWithHadithCount?

    @Query(
        """
            SELECT books.*, COUNT(hadiths.id) AS hadith_count
            FROM books
            LEFT JOIN hadiths
                ON hadiths.book_id = books.id
            WHERE books.id IN (:bookIds)
            GROUP BY books.id
        """
    )
    suspend fun getBooksByIds(bookIds: List<String>): List<BookWithHadithCount>

    @Query("SELECT * FROM book_translations WHERE book_id IN (:bookIds) AND lang IN (:langCodes)")
    suspend fun getBookTranslations(
        bookIds: List<String>,
        langCodes: List<String>,
    ): List<BookTranslationEntity>

    @Query(
        """SELECT * FROM hadiths WHERE hadiths.id = :id """
    )
    suspend fun getHadithById(id: String): HadithEntity?

    @Query("SELECT id FROM hadiths WHERE urn = :urn LIMIT 1")
    suspend fun getHadithIdByUrn(urn: Long): String?

    @Query(
        """SELECT * FROM hadiths WHERE hadiths.id IN (:ids) """
    )
    suspend fun getHadithsByIds(ids: List<String>): List<HadithEntity>

    @Query("SELECT * FROM hadith_contents WHERE hadith_id IN (:hadithIds) AND lang IN (:langCodes)")
    suspend fun getHadithContents(
        hadithIds: List<String>,
        langCodes: List<String>,
    ): List<HadithContentEntity>

    @Query("SELECT * FROM hadith_grades WHERE hadith_id IN (:hadithIds) AND lang IN (:langCodes)")
    suspend fun getHadithGrades(
        hadithIds: List<String>,
        langCodes: List<String>,
    ): List<HadithGradeEntity>

    @Query("SELECT urn, id FROM hadiths WHERE urn IN (:urns)")
    suspend fun getHadithIdsByUrns(urns: List<Long>): List<HadithIdUrnLookup>

    @Query("SELECT id FROM hadiths WHERE collection_id = :collectionId")
    suspend fun getHadithIdsForCollection(collectionId: String): List<String>

    @Query("SELECT * FROM hadiths WHERE book_id = :bookId ORDER BY urn")
    suspend fun getHadithsForBook(bookId: String): List<HadithEntity>

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

    @Query("SELECT * FROM hadiths WHERE book_id = :bookId ORDER BY urn LIMIT :limit OFFSET :offset")
    suspend fun getHadithsForBookPage(
        bookId: String,
        limit: Int,
        offset: Int,
    ): List<HadithEntity>

    @Query("SELECT * FROM chapters WHERE book_id = :bookId")
    suspend fun getChaptersForBook(bookId: String): List<ChapterEntity>

    @Query("SELECT * FROM chapter_translations WHERE chapter_id IN (:chapterIds) AND lang IN (:langCodes)")
    suspend fun getChapterTranslations(
        chapterIds: List<String>,
        langCodes: List<String>,
    ): List<ChapterTranslationEntity>

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

    @Query(
        """
        SELECT
            h.id AS hadith_id,
            h.book_id AS book_id,
            h.collection_id AS collection_id,
            h.number AS hadith_number,
            ct.title AS collection_name,
            hc.blocks_json AS blocks_json
        FROM hadiths AS h
        INNER JOIN hadith_contents AS hc
            ON h.id = hc.hadith_id AND hc.lang = :langCode
        INNER JOIN collection_translations AS ct
            ON h.collection_id = ct.collection_id AND ct.lang = :langCode
        WHERE hc.blocks_json LIKE '%' || :query || '%' COLLATE NOCASE
            AND (
                COALESCE(:collectionIds, '') = ''
                OR h.collection_id IN (:collectionIds)
            )
        ORDER BY h.urn
        """
    )
    fun searchHadiths(
        query: String,
        collectionIds: List<String>?,
        langCode: String,
    ): PagingSource<Int, HadithSearchRow>

    @Query(
        """
        SELECT
            b.id AS id,
            b.collection_id AS collection_id,
            b.number AS number,
            ct.title AS collection_name,
            bt_en.title AS title_en,
            bt_ar.title AS title_ar,
            (
                SELECT COUNT(*)
                FROM hadiths AS h
                WHERE h.book_id = b.id
            ) AS hadith_count
        FROM books AS b
        INNER JOIN book_translations AS bt_en
            ON b.id = bt_en.book_id AND bt_en.lang = :langCode
        INNER JOIN collection_translations AS ct
            ON b.collection_id = ct.collection_id AND ct.lang = :langCode
        LEFT JOIN book_translations AS bt_ar
            ON b.id = bt_ar.book_id AND bt_ar.lang = 'ar'
        WHERE (
            bt_en.title LIKE '%' || :query || '%' COLLATE NOCASE
            OR bt_en.intro LIKE '%' || :query || '%' COLLATE NOCASE
            OR bt_en.notes LIKE '%' || :query || '%' COLLATE NOCASE
            OR bt_en.preamble LIKE '%' || :query || '%' COLLATE NOCASE
        )
        AND (
            COALESCE(:collectionIds, '') = ''
            OR b.collection_id IN (:collectionIds)
        )
        ORDER BY ct.title, b.number + 0
        """
    )
    fun searchBooks(
        query: String,
        collectionIds: List<String>?,
        langCode: String,
    ): PagingSource<Int, BooksSearchResult>

    @Query(
        """
        SELECT
            h.id AS hadith_id,
            h.book_id AS book_id,
            h.collection_id AS collection_id,
            h.number AS hadith_number,
            ct.title AS collection_name,
            b.number AS book_number,
            bt.title AS book_title,
            (
                SELECT COUNT(*)
                FROM hadiths AS counted
                WHERE counted.book_id = h.book_id
                    AND counted.urn <= h.urn
            ) AS hadith_order
        FROM hadiths AS h
        INNER JOIN books AS b ON h.book_id = b.id
        INNER JOIN book_translations AS bt
            ON b.id = bt.book_id AND bt.lang = :langCode
        INNER JOIN collection_translations AS ct
            ON h.collection_id = ct.collection_id AND ct.lang = :langCode
        WHERE h.number = :hadithNumber COLLATE NOCASE
        ORDER BY h.urn
        """
    )
    suspend fun searchQuickHadithsByHadithNumber(
        hadithNumber: String,
        langCode: String,
    ): List<HadithSearchQuickResult>

    @Query(
        """
        SELECT
            h.id AS hadith_id,
            h.book_id AS book_id,
            h.collection_id AS collection_id,
            h.number AS hadith_number,
            ct.title AS collection_name,
            b.number AS book_number,
            bt.title AS book_title,
            (
                SELECT COUNT(*)
                FROM hadiths AS counted
                WHERE counted.book_id = h.book_id
                    AND counted.urn <= h.urn
            ) AS hadith_order
        FROM hadiths AS h
        INNER JOIN books AS b ON h.book_id = b.id
        INNER JOIN book_translations AS bt
            ON b.id = bt.book_id AND bt.lang = :langCode
        INNER JOIN collection_translations AS ct
            ON h.collection_id = ct.collection_id AND ct.lang = :langCode
        WHERE b.number = :bookNumber
        ORDER BY h.urn
        LIMIT 1 OFFSET :offset
        """
    )
    suspend fun searchQuickHadithByBookOrder(
        bookNumber: String,
        offset: Int,
        langCode: String,
    ): List<HadithSearchQuickResult>

    @Query(
        """
        SELECT
            b.id AS book_id,
            b.collection_id AS collection_id,
            b.number AS book_number,
            ct.title AS collection_name,
            bt.title AS book_title
        FROM books AS b
        INNER JOIN book_translations AS bt
            ON b.id = bt.book_id AND bt.lang = :langCode
        INNER JOIN collection_translations AS ct
            ON b.collection_id = ct.collection_id AND ct.lang = :langCode
        WHERE b.number = :bookNumber
        ORDER BY b.number + 0
        """
    )
    suspend fun searchQuickBooks(
        bookNumber: String,
        langCode: String,
    ): List<BookSearchQuickResult>
}
