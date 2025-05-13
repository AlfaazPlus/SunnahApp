package com.alfaazplus.sunnah.db.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alfaazplus.sunnah.db.dao.ScholarsDao
import com.alfaazplus.sunnah.db.models.scholars.Scholar

@Database(
    entities = [
        Scholar::class,
    ],
    version = 1,
)
abstract class ScholarsDatabase : RoomDatabase() {
    abstract val scholarsDao: ScholarsDao
}