package com.alfaazplus.sunnah.db.databases

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.alfaazplus.sunnah.db.dao.SearchIndexDao
import com.alfaazplus.sunnah.db.entities.search.SearchContentEntity
import com.alfaazplus.sunnah.db.entities.search.SearchFtsEntity
import com.alfaazplus.sunnah.db.entities.search.SearchIndexMetaEntity

const val SEARCH_INDEX_DB_VERSION = 1

@Database(
    entities = [
        SearchContentEntity::class,
        SearchFtsEntity::class,
        SearchIndexMetaEntity::class,
    ],
    version = SEARCH_INDEX_DB_VERSION,
    exportSchema = false,
)
abstract class SearchIndexDatabase : RoomDatabase() {
    abstract val searchIndexDao: SearchIndexDao
}
