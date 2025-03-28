package com.alfaazplus.sunnah.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
import com.alfaazplus.sunnah.ui.models.BooksSearchResult
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

    @Query("SELECT * FROM book WHERE collection_id = :collectionId")
    suspend fun getBookList(collectionId: Int): List<HBook>

    @Query("SELECT * FROM book_info WHERE collection_id=:collectionId AND book_id = :bookId AND language_code = :langCode")
    suspend fun getBookInfoById(langCode: String, collectionId: Int, bookId: Int): HBookInfo

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

    @Query("SELECT * FROM hadith WHERE collection_id = :collectionId AND book_id = :bookId AND chapter_id = :chapterId")
    suspend fun getHadithListByChapter(collectionId: Int, bookId: Int, chapterId: Int): List<Hadith>

    @Query("DELETE FROM collection WHERE collection_id = :collectionId")
    suspend fun deleteCollection(collectionId: Int)

    @Query("SELECT narrators2 from hadith WHERE urn = :urn")
    suspend fun getNarratorIds(urn: Int): String

    // search hadiths in collectionIds
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
            (hadith_translation.hadith_text LIKE '%' || :query || '%')
            AND (COALESCE(:collectionIds, '') = '' OR hadith.collection_id IN (:collectionIds))
            AND hadith_translation.lang_code = :langCode
            AND collection_info.language_code = :langCode
        """
    )
    fun searchHadiths(query: String, collectionIds: List<Int>?, langCode: String): PagingSource<Int, HadithSearchResult>

    // search books in collectionIds
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
            WHERE (book_info.title LIKE '%' || :query || '%' OR book_info.intro LIKE '%' || :query || '%' OR book_info.description LIKE '%' || :query || '%')
                AND book_info.language_code = :langCode
                AND collection_info.language_code = :langCode
                AND (COALESCE(:collectionIds, '') = '' OR book.collection_id IN (:collectionIds))
            """
    )
    fun searchBooks(query: String, collectionIds: List<Int>?, langCode: String): PagingSource<Int, BooksSearchResult>

    @Query(
        """
            SELECT * FROM hadith_translation
            INNER JOIN hadith
                ON hadith.urn = hadith_translation.ar_urn
            WHERE
                LENGTH(hadith_translation.hadith_text) <= :maxLength
                AND hadith_translation.lang_code = :langCode
                AND hadith_translation.grades LIKE '%sahih%'
            ORDER BY RANDOM() LIMIT 1                
        """
    )
    fun getNewHotd(maxLength: Int, langCode: String): HadithOfTheDay?

    @Query(
        """
            SELECT * FROM hadith_translation
            INNER JOIN hadith
                ON hadith.urn = hadith_translation.ar_urn
            WHERE
                hadith.urn = :urn
                AND hadith_translation.lang_code = :langCode
        """
    )
    fun getHotd(urn: String, langCode: String): HadithOfTheDay?
}