package com.alfaazplus.sunnah.db.databases

import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.TypeConverters
import com.alfaazplus.sunnah.db.converters.UserDbConverters
import com.alfaazplus.sunnah.db.dao.UserDataDao
import com.alfaazplus.sunnah.db.entities.userdata.v2.ReadHistory
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserBookmark
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserCollection
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserCollectionItem

@Database(
    entities = [
        UserCollection::class,
        UserCollectionItem::class,
        UserBookmark::class,
        ReadHistory::class,
    ],
    version = 1,
)
@TypeConverters(UserDbConverters::class)
abstract class UserDatabase : RoomDatabase() {
    abstract val dao: UserDataDao
}
