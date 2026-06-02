package com.alfaazplus.sunnah.db.entities.search

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "search_content",
    indices = [
        Index(value = ["hadith_id", "langCode"], unique = true),
    ],
)
data class SearchContentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "hadith_id")
    val hadithId: String,

    @ColumnInfo(name = "collection_id")
    val collectionId: String,

    @ColumnInfo(name = "langCode")
    val langCode: String,

    @ColumnInfo(name = "text")
    val text: String,
)
