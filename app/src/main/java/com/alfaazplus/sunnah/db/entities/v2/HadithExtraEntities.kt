package com.alfaazplus.sunnah.db.entities.v2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class HadithReferenceType(val type: String) {
    APP_REFERENCE("app_reference"),
    SUNNAHCOM_REFERENCE("sunnahcom_reference"),
    US_MSA_REFERENCE("us_msa_reference"),
    IN_BOOK_REFERENCE("in_book_reference"),
    ARABIC_REFERENCE("arabic_reference"),
    ARABIC_ENGLISH_BOOK_REFERENCE("arabic_english_book_reference"),
    ENGLISH_TRANSLATION("english_translation"),
    ;

    companion object {
        private val byType: Map<String, HadithReferenceType> = entries.associateBy { it.type }

        fun fromValue(value: String): HadithReferenceType =
            byType[value] ?: error("Unknown hadith reference type: $value")
    }
}

@Entity(
    tableName = "hadith_references",
    foreignKeys = [
        ForeignKey(
            entity = HadithEntity::class,
            parentColumns = ["id"],
            childColumns = ["hadith_id"],
        ),
    ],
    indices = [
        Index(value = ["hadith_id"]),
        Index(value = ["type"]),
    ],
)
data class HadithReferenceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "hadith_id")
    val hadithId: String,
    @ColumnInfo(name = "type")
    val type: HadithReferenceType,
    @ColumnInfo(name = "value")
    val value: String,
)


@Entity(
    tableName = "hadith_related",
    primaryKeys = ["hadith_id", "related_hadith_id"],
    foreignKeys = [
        ForeignKey(
            entity = HadithEntity::class,
            parentColumns = ["id"],
            childColumns = ["hadith_id"],
        ),
        ForeignKey(
            entity = HadithEntity::class,
            parentColumns = ["id"],
            childColumns = ["related_hadith_id"],
        ),
    ],
    indices = [
        Index(value = ["related_hadith_id"]),
    ],
)
data class HadithRelatedEntity(
    @ColumnInfo(name = "hadith_id")
    val hadithId: String,
    @ColumnInfo(name = "related_hadith_id")
    val relatedHadithId: String,
)

enum class HadithGradeType(val type: String) {
    SAHIH("sahih"),
    SAHIH_MAQTU("sahih_maqtu"),
    SAHIH_MARFU("sahih_marfu"),
    SAHIH_MAUQUF("sahih_mauquf"),
    SAHIH_MAUQUF_MARFU("sahih_mauquf_marfu"),
    SAHIH_MUTAWATIR("sahih_mutawatir"),
    HASAN("hasan"),
    HASAN_SAHIH("hasan_sahih"),
    HASAN_MAQTU("hasan_maqtu"),
    HASAN_MAUQUF("hasan_mauquf"),
    HASAN_LI_GHAIRIH("hasan_li_ghairih"),
    DAIF("daif"),
    DAIF_JIDDAN("daif_jiddan"),
    DAIF_MAQTU("daif_maqtu"),
    DAIF_MAQTU_MUNKAR("daif_maqtu_munkar"),
    DAIF_MARFU("daif_marfu"),
    DAIF_MAUQUF("daif_mauquf"),
    DAIF_MUNKAR("daif_munkar"),
    DAIF_MURSAL("daif_mursal"),
    MAQTU("maqtu"),
    MAUQUF("mauquf"),
    MUNKAR("munkar"),
    MAWDU("mawdu"),
    SHADH("shadh"),
    SHADH_ANHA("shadh_anha"),
    SHADH_MAQTU("shadh_maqtu"),
    OTHER("other"),
    ;

    companion object {
        private val byType: Map<String, HadithGradeType> = entries.associateBy { it.type }

        fun fromValue(type: String): HadithGradeType = byType[type] ?: OTHER
    }
}

@Entity(
    tableName = "hadith_grades",
    primaryKeys = ["hadith_id", "grade_id", "label", "lang"],
    foreignKeys = [
        ForeignKey(
            entity = HadithEntity::class,
            parentColumns = ["id"],
            childColumns = ["hadith_id"],
        ),
    ],
    indices = [
        Index(value = ["hadith_id"]),
        Index(value = ["grade_id"]),
        Index(value = ["lang"]),
    ],
)
data class HadithGradeEntity(
    @ColumnInfo(name = "hadith_id")
    val hadithId: String,
    @ColumnInfo(name = "grade_id")
    val gradeType: HadithGradeType,
    @ColumnInfo(name = "label")
    val label: String,
    @ColumnInfo(name = "lang")
    val lang: String = "en",
)

@Entity(
    tableName = "hadith_narrators",
    primaryKeys = ["hadith_id", "source", "narrator_id", "position"],
    foreignKeys = [
        ForeignKey(
            entity = HadithEntity::class,
            parentColumns = ["id"],
            childColumns = ["hadith_id"],
        ),
    ],
    indices = [
        Index(value = ["narrator_id"]),
    ],
)
data class HadithNarratorEntity(
    @ColumnInfo(name = "hadith_id")
    val hadithId: String,
    @ColumnInfo(name = "source")
    val source: String,
    @ColumnInfo(name = "narrator_id")
    val narratorId: Int,
    @ColumnInfo(name = "position")
    val position: Int,
)
