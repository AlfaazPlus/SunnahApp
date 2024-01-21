package com.alfaazplus.sunnah.db.models.hadith.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.alfaazplus.sunnah.db.contracts.BookContract
import com.alfaazplus.sunnah.db.contracts.BookContract.Columns
import com.alfaazplus.sunnah.db.contracts.CollectionContract

@Entity(
    tableName = BookContract.TABLE_NAME,
    primaryKeys = [Columns.ID, Columns.COLLECTION_ID],
    foreignKeys = [
        ForeignKey(
            entity = HCollection::class,
            parentColumns = [CollectionContract.Columns.ID],
            childColumns = [Columns.COLLECTION_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = [Columns.COLLECTION_ID]),
    ]
)
data class HBook(
    @ColumnInfo(name = Columns.ID) val id: Int,
    @ColumnInfo(name = Columns.COLLECTION_ID) val collectionId: Int,
    @ColumnInfo(name = Columns.SERIAL_NUMBER) val serialNumber: String,
    @ColumnInfo(name = Columns.ORDER_IN_COLLECTION) val orderInCollection: Int,
    @ColumnInfo(name = Columns.HADITH_START) val hadithStart: Int,
    @ColumnInfo(name = Columns.HADITH_END) val hadithEnd: Int,
    @ColumnInfo(name = Columns.HADITH_COUNT) val hadithCount: Int,
    @ColumnInfo(name = Columns.TITLE) val title: String,
    @ColumnInfo(name = Columns.INTRO) val intro: String?,
    @ColumnInfo(name = Columns.DESCRIPTION) val description: String?,
)
