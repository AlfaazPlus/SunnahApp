package com.alfaazplus.sunnah.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alfaazplus.sunnah.db.models.hadith.HadithChapter
import com.alfaazplus.sunnah.db.models.hadith.entities.HBook
import com.alfaazplus.sunnah.db.models.hadith.entities.HBookInfo
import com.alfaazplus.sunnah.db.models.hadith.entities.HChapter
import com.alfaazplus.sunnah.db.models.hadith.entities.HChapterInfo
import com.alfaazplus.sunnah.db.models.hadith.entities.HCollection
import com.alfaazplus.sunnah.db.models.hadith.entities.HCollectionInfo
import com.alfaazplus.sunnah.db.models.hadith.entities.Hadith
import com.alfaazplus.sunnah.db.models.hadith.entities.HadithTranslation

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

    @Query("SELECT * FROM book_info WHERE book_id = :bookId AND language_code = :langCode")
    suspend fun getBookInfoById(langCode: String, bookId: Int): HBookInfo

    @Query("SELECT * FROM chapter INNER JOIN chapter_info ON chapter.chapter_id = chapter_info.chapter_id WHERE chapter.book_id = :bookId AND chapter.collection_id = :collectionId AND chapter_info.book_id = :bookId AND chapter_info.collection_id = :collectionId AND chapter_info.language_code = :langCode")
    suspend fun getChapterList(langCode: String, collectionId: Int, bookId: Int): List<HadithChapter>

    @Query("SELECT COUNT(*) FROM hadith WHERE collection_id = :collectionId AND book_id = :bookId")
    suspend fun getHadithCount(collectionId: Int, bookId: Int): Int

    @Query("SELECT * FROM hadith WHERE collection_id = :collectionId AND book_id = :bookId")
    suspend fun getHadithList(collectionId: Int, bookId: Int): List<Hadith>

    @Query("SELECT * FROM hadith WHERE collection_id = :collectionId AND book_id = :bookId AND order_in_book = :orderInBook")
    suspend fun getHadithByOrder(collectionId: Int, bookId: Int, orderInBook: Int): Hadith

    @Query("SELECT * FROM hadith_translation WHERE ar_urn = :arURN AND language_code = :langCode")
    suspend fun getHadithTranslationByArURN(arURN: String, langCode: String): HadithTranslation

    @Query("SELECT * FROM hadith WHERE collection_id = :collectionId AND book_id = :bookId AND chapter_id = :chapterId")
    suspend fun getHadithListByChapter(collectionId: Int, bookId: Int, chapterId: Int): List<Hadith>

    @Query("DELETE FROM collection WHERE collection_id = :collectionId")
    suspend fun deleteCollection(collectionId: Int)
}