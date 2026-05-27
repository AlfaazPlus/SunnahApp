package com.alfaazplus.sunnah.db.entities.hadith.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.alfaazplus.sunnah.db.contracts.CollectionContract
import com.alfaazplus.sunnah.db.contracts.CollectionInfoContract
import com.alfaazplus.sunnah.db.contracts.CollectionInfoContract.Columns

@Entity(
    tableName = CollectionInfoContract.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = HCollection::class,
            parentColumns = [CollectionContract.Columns.ID],
            childColumns = [Columns.COLLECTION_ID],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(value = [Columns.COLLECTION_ID])
    ]
)
@Deprecated("v2")
data class HCollectionInfo(
    @ColumnInfo(name = Columns.ID) @PrimaryKey val id: Int,
    @ColumnInfo(name = Columns.COLLECTION_ID) val collectionId: Int,
    @ColumnInfo(name = Columns.NAME) val name: String,
    @ColumnInfo(name = Columns.INTRO) val intro: String?,
    @ColumnInfo(name = Columns.DESCRIPTION) val description: String?,
    @ColumnInfo(name = Columns.NUMBERING_SOURCE) val numberingSource: String?,
    @ColumnInfo(name = Columns.LANGUAGE_CODE) val languageCode: String,
)
