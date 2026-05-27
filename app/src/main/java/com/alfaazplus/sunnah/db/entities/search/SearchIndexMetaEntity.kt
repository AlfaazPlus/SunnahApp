package com.alfaazplus.sunnah.db.entities.search

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "search_index_meta")
data class SearchIndexMetaEntity(
    @PrimaryKey
    @ColumnInfo(name = "key")
    val key: String,

    @ColumnInfo(name = "fingerprint")
    val fingerprint: String,
)
