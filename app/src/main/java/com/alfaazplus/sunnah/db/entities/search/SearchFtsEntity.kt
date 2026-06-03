package com.alfaazplus.sunnah.db.entities.search

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Fts5
import androidx.room3.FtsOptions

@Fts5(
    contentEntity = SearchContentEntity::class,
    contentRowId = "id",
    tokenizer = FtsOptions.TOKENIZER_UNICODE61,
    tokenizerArgs = ["remove_diacritics", "2"],
    notIndexed = ["langCode", "hadith_id", "collection_id"],
    prefix = [2, 3, 4],
    hasColumnSize = false,
    detail = FtsOptions.Detail.COLUMN,
)
@Entity(tableName = "search_fts")
data class SearchFtsEntity(
    @ColumnInfo(name = "langCode")
    val langCode: String,

    @ColumnInfo(name = "hadith_id")
    val hadithId: String,

    @ColumnInfo(name = "collection_id")
    val collectionId: String,

    @ColumnInfo(name = "text")
    val text: String,
)
