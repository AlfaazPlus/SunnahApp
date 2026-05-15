package com.alfaazplus.sunnah.db.entities.v2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.alfaazplus.sunnah.api.JsonHelper
import com.alfaazplus.sunnah.db.interfaces.HadithMethods
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(
    tableName = "hadiths",
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
        ForeignKey(
            entity = ChapterEntity::class,
            parentColumns = ["id"],
            childColumns = ["chapter_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index(value = ["collection_id"]),
        Index(value = ["book_id"]),
        Index(value = ["chapter_id"]),
        Index(value = ["number"]),
        Index(
            value = ["urn"], unique = true
        ),
    ],
)
data class HadithEntity(
    /**
     * Global unique id
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "urn")
    val urn: Long?,
    @ColumnInfo(name = "collection_id")
    override val collectionId: String,
    @ColumnInfo(name = "book_id")
    override val bookId: String,
    @ColumnInfo(name = "chapter_id")
    val chapterId: String?,
    @ColumnInfo(name = "number")
    val number: String?,
) : HadithMethods {
    override val hadithId get() = id
}

enum class HadithBlockType(val type: String) {
    NARRATOR("narrator"),
    SANAD("sanad"),
    MATN("matn"),
    COMMENTARY("commentary"),
    NOTE("note"),
    TRANSLATION("translation"),
    UNKNOWN("unknown");

    companion object {
        fun fromValue(value: String): HadithBlockType = entries.find { it.type == value } ?: UNKNOWN
    }
}

@Serializable
data class HadithBlock(
    @SerialName("type")
    private val _type: String,
    @SerialName("text")
    val text: String? = null,
) {
    val type = HadithBlockType.fromValue(_type)
}

@Entity(
    tableName = "hadith_contents",
    primaryKeys = ["hadith_id", "lang"],
    foreignKeys = [
        ForeignKey(
            entity = HadithEntity::class,
            parentColumns = ["id"],
            childColumns = ["hadith_id"],
        ),
    ],
    indices = [
        Index(value = ["lang"]),
    ],
)
data class HadithContentEntity(
    @ColumnInfo(name = "hadith_id")
    val hadithId: String,
    @ColumnInfo(name = "lang")
    val lang: String,
    @ColumnInfo(name = "blocks_json")
    val blocksJson: String,
) {
    @get:Ignore
    val blocks: List<HadithBlock> by lazy {
        JsonHelper.json.decodeFromString(blocksJson)
    }
}
