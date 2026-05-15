package com.alfaazplus.sunnah.db.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alfaazplus.sunnah.db.dao.UserDataDao
import com.alfaazplus.sunnah.db.entities.userdata.ReadHistory
import com.alfaazplus.sunnah.db.entities.userdata.UserBookmark
import com.alfaazplus.sunnah.db.entities.userdata.UserCollection
import com.alfaazplus.sunnah.db.entities.userdata.UserCollectionItem
import com.alfaazplus.sunnah.db.converters.UserDbConverters

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
