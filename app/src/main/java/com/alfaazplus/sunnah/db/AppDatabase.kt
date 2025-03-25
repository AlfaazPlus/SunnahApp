package com.alfaazplus.sunnah.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alfaazplus.sunnah.db.dao.HadithDao
import com.alfaazplus.sunnah.db.models.hadith.entities.HBook
import com.alfaazplus.sunnah.db.models.hadith.entities.HBookInfo
import com.alfaazplus.sunnah.db.models.hadith.entities.HChapter
import com.alfaazplus.sunnah.db.models.hadith.entities.HChapterInfo
import com.alfaazplus.sunnah.db.models.hadith.entities.HCollection
import com.alfaazplus.sunnah.db.models.hadith.entities.HCollectionInfo
import com.alfaazplus.sunnah.db.models.hadith.entities.Hadith
import com.alfaazplus.sunnah.db.models.hadith.entities.HadithTranslation
import com.alfaazplus.sunnah.db.models.scholars.Scholar

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
abstract class AppDatabase : RoomDatabase() {
    abstract val hadithDao: HadithDao
}