package com.alfaazplus.sunnah.db.entities.v2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chapters",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collection_id"],
        ),
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["book_id"],
        ),
    ],
    indices = [Index(value = ["collection_id"]), Index(value = ["book_id"])],
)
data class ChapterEntity(
    /**
     * Global unique id
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "collection_id")
    val collectionId: String,
    @ColumnInfo(name = "book_id")
    val bookId: String,
    @ColumnInfo(name = "number")
    val number: String?,
)

@Entity(
    tableName = "chapter_translations", primaryKeys = ["chapter_id", "lang"], foreignKeys = [ForeignKey(
        entity = ChapterEntity::class, parentColumns = ["id"], childColumns = ["chapter_id"]
    )], indices = [Index(value = ["lang"])]
)
data class ChapterTranslationEntity(
    @ColumnInfo(name = "chapter_id")
    val chapterId: String,
    @ColumnInfo(name = "lang")
    val lang: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "intro")
    val intro: String?,
)
