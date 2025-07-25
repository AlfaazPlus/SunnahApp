package com.alfaazplus.sunnah.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.alfaazplus.sunnah.db.models.HadithOfTheDay
import com.alfaazplus.sunnah.db.models.hadith.HadithChapter
import com.alfaazplus.sunnah.db.models.hadith.entities.HBook
import com.alfaazplus.sunnah.db.models.hadith.entities.HBookInfo
import com.alfaazplus.sunnah.db.models.hadith.entities.HChapter
import com.alfaazplus.sunnah.db.models.hadith.entities.HChapterInfo
import com.alfaazplus.sunnah.db.models.hadith.entities.HCollection
import com.alfaazplus.sunnah.db.models.hadith.entities.HCollectionInfo
import com.alfaazplus.sunnah.db.models.hadith.entities.Hadith
import com.alfaazplus.sunnah.db.models.hadith.entities.HadithTranslation
import com.alfaazplus.sunnah.ui.models.BookSearchQuickResult
import com.alfaazplus.sunnah.ui.models.BooksSearchResult
import com.alfaazplus.sunnah.ui.models.HadithSearchQuickResult
import com.alfaazplus.sunnah.ui.models.HadithSearchResult

@Dao
interface HadithDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: HCollection): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionInfo(collectionInfo: HCollectionInfo): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: HBook): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookInfo(bookInfo: HBookInfo): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: HChapter): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapterInfo(chapterInfo: HChapterInfo): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHadith(hadith: Hadith): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHadithTranslation(hadithTranslation: HadithTranslation): Long

    @Query("SELECT * FROM collection")
    suspend fun getCollectionList(): List<HCollection>

    @Query("SELECT * FROM collection WHERE collection_id = :collectionId")
    suspend fun getCollectionById(collectionId: Int): HCollection

    @Query("SELECT * FROM collection_info WHERE collection_id = :collectionId AND language_code = :langCode")
    suspend fun getCollectionInfoById(langCode: String, collectionId: Int): HCollectionInfo

    @Query("SELECT * FROM book WHERE collection_id = :collectionId AND book_id = :bookId")
    suspend fun getBookById(collectionId: Int, bookId: Int): HBook

    @Query("SELECT * FROM book WHERE collection_id = :collectionId")
    suspend fun getBookList(collectionId: Int): List<HBook>

    @Query("SELECT * FROM book_info WHERE collection_id=:collectionId AND book_id = :bookId AND language_code = :langCode")
    suspend fun getBookInfoById(langCode: String, collectionId: Int, bookId: Int): HBookInfo

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM chapter INNER JOIN chapter_info ON chapter.chapter_id = chapter_info.chapter_id WHERE chapter.chapter_id = :chapterId")
    suspend fun getChapterWithInfoById(chapterId: Double): HadithChapter

    @Query("SELECT COUNT(*) FROM hadith WHERE collection_id = :collectionId AND book_id = :bookId")
    suspend fun getHadithCount(collectionId: Int, bookId: Int): Int

    @Query("SELECT * FROM hadith WHERE collection_id = :collectionId AND book_id = :bookId")
    suspend fun getHadithList(collectionId: Int, bookId: Int): List<Hadith>

    @Query("SELECT * FROM hadith WHERE collection_id = :collectionId AND book_id = :bookId AND order_in_book = :orderInBook")
    suspend fun getHadithByOrder(collectionId: Int, bookId: Int, orderInBook: Int): Hadith

    @Query("SELECT * FROM hadith WHERE urn = :urn")
    suspend fun getHadithByURN(urn: String): Hadith

    @Query("SELECT * FROM hadith_translation WHERE ar_urn = :arURN AND lang_code = :langCode")
    suspend fun getHadithTranslationByArURN(arURN: String, langCode: String): HadithTranslation

    @Query(
        """
        SELECT * FROM hadith_translation 
        WHERE ar_urn = (
            SELECT urn FROM hadith 
            WHERE collection_id = :collectionId AND book_id = :bookId AND hadith_number = :hadithNumber
        ) AND lang_code = :langCode
    """
    )
    suspend fun getHadithTranslationByHadithNumber(
        collectionId: Int,
        bookId: Int,
        hadithNumber: String,
        langCode: String,
    ): HadithTranslation

    @Query("SELECT * FROM hadith WHERE collection_id = :collectionId AND book_id = :bookId AND chapter_id = :chapterId")
    suspend fun getHadithListByChapter(collectionId: Int, bookId: Int, chapterId: Int): List<Hadith>

    @Query("DELETE FROM collection WHERE collection_id = :collectionId")
    suspend fun deleteCollection(collectionId: Int)

    @Query("SELECT narrators2 from hadith WHERE urn = :urn")
    suspend fun getNarratorIds(urn: Int): String

    // search hadiths in collectionIds
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT
            hadith.*,
            hadith_translation.*,
            collection_info.name
        FROM hadith
        INNER JOIN hadith_translation 
            ON hadith.urn = hadith_translation.ar_urn
        INNER JOIN collection_info
            ON hadith.collection_id = collection_info.collection_id
        WHERE 
            (hadith_translation.hadith_text LIKE '%' || :query || '%' COLLATE NOCASE)
            AND (COALESCE(:collectionIds, '') = '' OR hadith.collection_id IN (:collectionIds))
            AND hadith_translation.lang_code = :langCode
            AND collection_info.language_code = :langCode
        """
    )
    fun searchHadiths(query: String, collectionIds: List<Int>?, langCode: String): PagingSource<Int, HadithSearchResult>

    // search books in collectionIds
    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
            SELECT 
                book.*,
                book_info.*,
                collection_info.name
            FROM book
            INNER JOIN book_info
                ON book.book_id = book_info.book_id AND book.collection_id = book_info.collection_id
            INNER JOIN collection_info
                ON book.collection_id = collection_info.collection_id
            WHERE (
                    book_info.title LIKE '%' || :query || '%' COLLATE NOCASE OR
                    book_info.intro LIKE '%' || :query || '%' COLLATE NOCASE OR
                    book_info.description LIKE '%' || :query || '%' COLLATE NOCASE
                )
                AND book_info.language_code = :langCode
                AND collection_info.language_code = :langCode
                AND (COALESCE(:collectionIds, '') = '' OR book.collection_id IN (:collectionIds))
            """
    )
    fun searchBooks(query: String, collectionIds: List<Int>?, langCode: String): PagingSource<Int, BooksSearchResult>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT
            hadith.hadith_number,
            hadith.collection_id,
            hadith.book_id,
            hadith.order_in_book,
            collection_info.name,
            book.serial_number,
            book_info.title
        FROM hadith
        INNER JOIN collection_info
            ON hadith.collection_id = collection_info.collection_id
        INNER JOIN book
            ON hadith.collection_id = book.collection_id AND hadith.book_id = book.book_id
        INNER JOIN book_info
            ON hadith.collection_id = book_info.collection_id AND hadith.book_id = book_info.book_id
        WHERE 
            hadith.hadith_number = :hadithNumber COLLATE NOCASE
        """
    )
    suspend fun searchQuickHadithsByHadithNumber(hadithNumber: String): List<HadithSearchQuickResult>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT
            hadith.hadith_number,
            hadith.collection_id,
            hadith.book_id,
            hadith.order_in_book,
            collection_info.name,
            book.serial_number,
            book_info.title
        FROM book
        INNER JOIN collection_info
            ON book.collection_id = collection_info.collection_id
        INNER JOIN book_info
            ON book.collection_id = book_info.collection_id AND book.book_id = book_info.book_id
        INNER JOIN hadith
            ON book.collection_id = hadith.collection_id AND book.book_id = hadith.book_id
        WHERE
            book.serial_number = :bookSerial
            AND hadith.order_in_book = :orderInBook
        """
    )
    suspend fun searchQuickHadithsByBook(bookSerial: String, orderInBook: Int): List<HadithSearchQuickResult>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT
            book.book_id,
            book.collection_id,
            book.serial_number,
            collection_info.name,
            book_info.title
        FROM book
        INNER JOIN collection_info
            ON book.collection_id = collection_info.collection_id
        INNER JOIN book_info
            ON book.collection_id = book_info.collection_id AND book.book_id = book_info.book_id
        WHERE 
            book.serial_number = :serialNumber
        """
    )
    suspend fun searchQuickBooks(serialNumber: String): List<BookSearchQuickResult>

    @Query(
        """
            SELECT ar_urn FROM hadith_translation
            WHERE
                LENGTH(hadith_text) <= :maxLength
                AND lang_code = :langCode
                AND grades LIKE '%sahih%'
            ORDER BY RANDOM() LIMIT 1                
        """
    )
    fun getNewHotdUrn(maxLength: Int, langCode: String): String?

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
    SELECT * FROM hadith
    INNER JOIN hadith_translation
      ON hadith.urn = hadith_translation.ar_urn
    WHERE
      hadith.urn = :urn
      AND hadith_translation.lang_code = :langCode
        """
    )
    fun getHotd(urn: String, langCode: String): HadithOfTheDay?
}