package com.alfaazplus.sunnah.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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
}
