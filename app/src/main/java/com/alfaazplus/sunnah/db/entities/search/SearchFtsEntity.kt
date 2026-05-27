package com.alfaazplus.sunnah.db.entities.search

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Fts5
import androidx.room3.FtsOptions

@Fts5(
    contentEntity = SearchContentEntity::class,
    tokenizer = FtsOptions.TOKENIZER_UNICODE61,
    tokenizerArgs = ["remove_diacritics", "2"],
    prefix = [2, 3, 4],
)
@Entity(tableName = "search_fts")
data class SearchFtsEntity(
    @ColumnInfo(name = "langCode")
    val langCode: String,

    @ColumnInfo(name = "hadith_id")
    val hadithId: String,

    @ColumnInfo(name = "text")
    val text: String,
)
