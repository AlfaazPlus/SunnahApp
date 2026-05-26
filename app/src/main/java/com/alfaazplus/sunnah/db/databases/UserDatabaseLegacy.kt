package com.alfaazplus.sunnah.db.databases

import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.TypeConverters
import com.alfaazplus.sunnah.db.dao.UserDataDaoLegacy
import com.alfaazplus.sunnah.db.entities.userdata.ReadHistoryLegacy
import com.alfaazplus.sunnah.db.entities.userdata.UserBookmarkLegacy
import com.alfaazplus.sunnah.db.entities.userdata.UserCollectionLegacy
import com.alfaazplus.sunnah.db.entities.userdata.UserCollectionItemLegacy
import com.alfaazplus.sunnah.db.converters.UserDbConverters

@Deprecated("v2")
@Database(
    entities = [
        UserCollectionLegacy::class,
        UserCollectionItemLegacy::class,
        UserBookmarkLegacy::class,
        ReadHistoryLegacy::class,
    ],
    version = 1,
)
@TypeConverters(UserDbConverters::class)
abstract class UserDatabaseLegacy : RoomDatabase() {
    abstract val dao: UserDataDaoLegacy
}
