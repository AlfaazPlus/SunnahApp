package com.alfaazplus.sunnah.db.entities.v2

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "books",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collection_id"],
        ),
    ],
    indices = [
        Index(value = ["collection_id"]),
    ],
)
data class BookEntity(
    /**
     * Global unique id
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "collection_id")
    val collectionId: String,
    @ColumnInfo(name = "number")
    val number: String?,
)

@Entity(
    tableName = "book_translations",
    primaryKeys = ["book_id", "lang"],
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["book_id"],
        ),
    ],
    indices = [
        Index(value = ["lang"]),
    ],
)
data class BookTranslationEntity(
    @ColumnInfo(name = "book_id")
    val bookId: String,
    @ColumnInfo(name = "lang")
    val lang: String,
    @ColumnInfo(name = "title")
    val title: String?,
    @ColumnInfo(name = "intro")
    val intro: String?,
    @ColumnInfo(name = "preamble")
    val preamble: String?,
    @ColumnInfo(name = "notes")
    val notes: String?,
)
