package com.alfaazplus.sunnah.db.entities.v2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class CollectionType(val type: String) {
    COLLECTION("collection"),
    SELECTION("selection");

    companion object {
        private val byType: Map<String, CollectionType> = CollectionType.entries.associateBy { it.type }
        fun fromType(type: String): CollectionType = byType[type] ?: error("Unknown hadith reference type: $type")
    }
}

@Entity(
    tableName = "collections"
)
data class CollectionEntity(
    /**
     * Global unique id
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "type")
    val type: CollectionType,
    @ColumnInfo(name = "has_volumes")
    val hasVolumes: Boolean,
    @ColumnInfo(name = "has_books")
    val hasBooks: Boolean,
    @ColumnInfo(name = "has_chapters")
    val hasChapters: Boolean,
    @ColumnInfo(name = "numbering_source")
    val numberingSource: String?,
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0,
)

@Entity(
    tableName = "collection_translations",
    primaryKeys = ["collection_id", "lang"],
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collection_id"],
        ),
    ],
    indices = [
        Index(value = ["lang"]),
    ],
)
data class CollectionTranslationEntity(
    @ColumnInfo(name = "collection_id")
    val collectionId: String,
    @ColumnInfo(name = "lang")
    val lang: String,
    @ColumnInfo(name = "title")
    val title: String?,
    @ColumnInfo(name = "intro")
    val intro: String?,
    @ColumnInfo(name = "description")
    val description: String?,
)
