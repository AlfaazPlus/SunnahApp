package com.alfaazplus.sunnah.db.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alfaazplus.sunnah.db.dao.HadithDaoLegacy
import com.alfaazplus.sunnah.db.entities.hadith.entities.HBook
import com.alfaazplus.sunnah.db.entities.hadith.entities.HBookInfo
import com.alfaazplus.sunnah.db.entities.hadith.entities.HChapter
import com.alfaazplus.sunnah.db.entities.hadith.entities.HChapterInfo
import com.alfaazplus.sunnah.db.entities.hadith.entities.HCollection
import com.alfaazplus.sunnah.db.entities.hadith.entities.HCollectionInfo
import com.alfaazplus.sunnah.db.entities.hadith.entities.Hadith
import com.alfaazplus.sunnah.db.entities.hadith.entities.HadithTranslation

@Database(
    entities = [
        HCollection::class,
        HCollectionInfo::class,
        HBook::class,
        HBookInfo::class,
        HChapter::class,
        HChapterInfo::class,
        Hadith::class,
        HadithTranslation::class,
    ],
    version = 1,
)
@Deprecated("")
abstract class HadithDatabaseLegacy : RoomDatabase() {
    abstract val hadithDao: HadithDaoLegacy
}
