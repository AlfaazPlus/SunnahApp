package com.alfaazplus.sunnah.db.models.hadith.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.alfaazplus.sunnah.db.contracts.HadithContract
import com.alfaazplus.sunnah.db.contracts.HadithTranslationContract
import com.alfaazplus.sunnah.db.contracts.HadithTranslationContract.Columns

@Entity(
    tableName = HadithTranslationContract.TABLE_NAME,
    indices = [
        Index(value = [Columns.URN], unique = true),
        Index(value = [Columns.AR_URN], unique = true),
    ],
    foreignKeys = [
        ForeignKey(
            entity = Hadith::class,
            parentColumns = [HadithContract.Columns.URN],
            childColumns = [Columns.AR_URN],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class HadithTranslation(
    @ColumnInfo(name = Columns.ID) @PrimaryKey val id: Int,
    @ColumnInfo(name = Columns.COLLECTION_ID) val collectionId: Int,
    @ColumnInfo(name = Columns.URN) val urn: String,
    @ColumnInfo(name = Columns.AR_URN) val arUrn: String,
    @ColumnInfo(name = Columns.NARRATOR_PREFIX) val narratorPrefix: String?,
    @ColumnInfo(name = Columns.HADITH_TEXT) val hadithText: String,
    @ColumnInfo(name = Columns.NARRATOR_SUFFIX) val narratorSuffix: String?,
    @ColumnInfo(name = Columns.COMMENTS) val comments: String?,
    @ColumnInfo(name = Columns.GRADES) val grades: String?,
    @ColumnInfo(name = Columns.REFERENCE) val reference: String?,
    @ColumnInfo(name = Columns.REFERENCE_IN_BOOK) val referenceInBook: String?,
    @ColumnInfo(name = Columns.REFERENCE_USC_MSA) val referenceUscMsa: String?,
    @ColumnInfo(name = Columns.REFERENCE_ENG) val referenceEn: String?,
    @ColumnInfo(name = Columns.LANGUAGE_CODE) val languageCode: String,
)