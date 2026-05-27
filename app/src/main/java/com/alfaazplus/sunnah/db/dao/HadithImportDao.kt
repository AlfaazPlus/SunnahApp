package com.alfaazplus.sunnah.db.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.alfaazplus.sunnah.db.entities.v2.BookEntity
import com.alfaazplus.sunnah.db.entities.v2.BookTranslationEntity
import com.alfaazplus.sunnah.db.entities.v2.ChapterEntity
import com.alfaazplus.sunnah.db.entities.v2.ChapterTranslationEntity
import com.alfaazplus.sunnah.db.entities.v2.CollectionEntity
import com.alfaazplus.sunnah.db.entities.v2.CollectionTranslationEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithContentEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithGradeEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithNarratorEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithReferenceEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithRelatedEntity


@Dao
interface HadithImportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollections(entities: List<CollectionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionTranslations(entities: List<CollectionTranslationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(entities: List<BookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookTranslations(entities: List<BookTranslationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(entities: List<ChapterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapterTranslations(entities: List<ChapterTranslationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHadiths(entities: List<HadithEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHadithContents(entities: List<HadithContentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHadithReferences(entities: List<HadithReferenceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHadithRelated(entities: List<HadithRelatedEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHadithGrades(entities: List<HadithGradeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHadithNarrators(entities: List<HadithNarratorEntity>)

    @Query(
        """
        DELETE FROM hadith_related
        WHERE hadith_id IN (SELECT id FROM hadiths WHERE collection_id = :collectionId)
            OR related_hadith_id IN (SELECT id FROM hadiths WHERE collection_id = :collectionId)
        """
    )
    suspend fun deleteHadithRelatedForCollection(collectionId: String)

    @Query(
        """
        DELETE FROM hadith_contents
        WHERE hadith_id IN (SELECT id FROM hadiths WHERE collection_id = :collectionId)
        """
    )
    suspend fun deleteHadithContentsForCollection(collectionId: String)

    @Query(
        """
        DELETE FROM hadith_references
        WHERE hadith_id IN (SELECT id FROM hadiths WHERE collection_id = :collectionId)
        """
    )
    suspend fun deleteHadithReferencesForCollection(collectionId: String)

    @Query(
        """
        DELETE FROM hadith_grades
        WHERE hadith_id IN (SELECT id FROM hadiths WHERE collection_id = :collectionId)
        """
    )
    suspend fun deleteHadithGradesForCollection(collectionId: String)

    @Query(
        """
        DELETE FROM hadith_narrators
        WHERE hadith_id IN (SELECT id FROM hadiths WHERE collection_id = :collectionId)
        """
    )
    suspend fun deleteHadithNarratorsForCollection(collectionId: String)

    @Query("DELETE FROM hadiths WHERE collection_id = :collectionId")
    suspend fun deleteHadithsForCollection(collectionId: String)

    @Query(
        """
        DELETE FROM chapter_translations
        WHERE chapter_id IN (SELECT id FROM chapters WHERE collection_id = :collectionId)
        """
    )
    suspend fun deleteChapterTranslationsForCollection(collectionId: String)

    @Query("DELETE FROM chapters WHERE collection_id = :collectionId")
    suspend fun deleteChaptersForCollection(collectionId: String)

    @Query(
        """
        DELETE FROM book_translations
        WHERE book_id IN (SELECT id FROM books WHERE collection_id = :collectionId)
        """
    )
    suspend fun deleteBookTranslationsForCollection(collectionId: String)

    @Query("DELETE FROM books WHERE collection_id = :collectionId")
    suspend fun deleteBooksForCollection(collectionId: String)

    @Query("DELETE FROM collection_translations WHERE collection_id = :collectionId")
    suspend fun deleteCollectionTranslations(collectionId: String)

    @Query("DELETE FROM collections WHERE id = :collectionId")
    suspend fun deleteCollection(collectionId: String)

    @Query("SELECT DISTINCT lang FROM hadith_contents WHERE lang IN (:langCodes)")
    suspend fun getDownloadedTranslations(langCodes: List<String>): List<String>

    @Query("DELETE FROM hadith_contents WHERE lang = :lang")
    suspend fun deleteHadithContentsForLang(lang: String)

    @Query("DELETE FROM hadith_grades WHERE lang = :lang")
    suspend fun deleteHadithGradesForLang(lang: String)

    @Query("DELETE FROM chapter_translations WHERE lang = :lang")
    suspend fun deleteChapterTranslationsForLang(lang: String)

    @Query("DELETE FROM book_translations WHERE lang = :lang")
    suspend fun deleteBookTranslationsForLang(lang: String)

    @Query("DELETE FROM collection_translations WHERE lang = :lang")
    suspend fun deleteCollectionTranslationsForLang(lang: String)

    @Transaction
    suspend fun deleteTranslationData(lang: String) {
        deleteHadithContentsForLang(lang)
        deleteHadithGradesForLang(lang)
        deleteChapterTranslationsForLang(lang)
        deleteBookTranslationsForLang(lang)
        deleteCollectionTranslationsForLang(lang)
    }

    @Transaction
    suspend fun deleteCollectionData(collectionId: String) {
        deleteHadithRelatedForCollection(collectionId)
        deleteHadithContentsForCollection(collectionId)
        deleteHadithReferencesForCollection(collectionId)
        deleteHadithGradesForCollection(collectionId)
        deleteHadithNarratorsForCollection(collectionId)
        deleteHadithsForCollection(collectionId)
        deleteChapterTranslationsForCollection(collectionId)
        deleteChaptersForCollection(collectionId)
        deleteBookTranslationsForCollection(collectionId)
        deleteBooksForCollection(collectionId)
        deleteCollectionTranslations(collectionId)
        deleteCollection(collectionId)
    }
}
