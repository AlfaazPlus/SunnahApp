package com.alfaazplus.sunnah.db.databases

import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import com.alfaazplus.sunnah.db.dao.ScholarsDao
import com.alfaazplus.sunnah.db.entities.scholars.Scholar

@Database(
    entities = [
        Scholar::class,
    ],
    version = 1,
)
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
abstract class ScholarsDatabase : RoomDatabase() {
    abstract val scholarsDao: ScholarsDao
}
