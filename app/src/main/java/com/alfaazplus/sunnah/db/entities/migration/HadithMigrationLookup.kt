package com.alfaazplus.sunnah.db.entities.migration

import androidx.room.ColumnInfo

data class LegacyHadithUrnLookup(
    @ColumnInfo(name = "collection_id")
    val collectionId: Int,
    @ColumnInfo(name = "book_id")
    val bookId: Int,
    @ColumnInfo(name = "hadith_number")
    val hadithNumber: String,
    @ColumnInfo(name = "urn")
    val urn: String,
)

data class HadithIdUrnLookup(
    @ColumnInfo(name = "urn")
    val urn: Long,
    @ColumnInfo(name = "id")
    val hadithId: String,
)
