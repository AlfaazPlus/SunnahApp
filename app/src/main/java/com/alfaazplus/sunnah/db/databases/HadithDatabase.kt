package com.alfaazplus.sunnah.db.databases

import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.TypeConverters
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import com.alfaazplus.sunnah.db.converters.HadithDbConverters
import com.alfaazplus.sunnah.db.dao.HadithDao
import com.alfaazplus.sunnah.db.dao.HadithImportDao
import com.alfaazplus.sunnah.db.dao.HadithSearchDao
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

@Database(
    entities = [
        CollectionEntity::class,
        CollectionTranslationEntity::class,
        BookEntity::class,
        BookTranslationEntity::class,
        ChapterEntity::class,
        ChapterTranslationEntity::class,
        HadithEntity::class,
        HadithContentEntity::class,
        HadithReferenceEntity::class,
        HadithRelatedEntity::class,
        HadithGradeEntity::class,
        HadithNarratorEntity::class,
    ],
    version = 1,
)
@TypeConverters(HadithDbConverters::class)
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
abstract class HadithDatabase : RoomDatabase() {
    abstract val hadithDao: HadithDao
    abstract val searchDao: HadithSearchDao
    abstract val importDao: HadithImportDao
}
