package com.alfaazplus.sunnah.db.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alfaazplus.sunnah.db.dao.UserDataDao
import com.alfaazplus.sunnah.db.models.userdata.UserBookmark
import com.alfaazplus.sunnah.db.models.userdata.UserCollection
import com.alfaazplus.sunnah.db.models.userdata.UserCollectionItem
import com.alfaazplus.sunnah.helpers.DbConverters

@Database(
    entities = [
        UserCollection::class,
        UserCollectionItem::class,
        UserBookmark::class,
    ],
    version = 1,
)
@TypeConverters(DbConverters::class)
abstract class UserDatabase : RoomDatabase() {
    abstract val dao: UserDataDao
}