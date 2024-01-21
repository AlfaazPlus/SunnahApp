package com.alfaazplus.sunnah.db.models.hadith.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.alfaazplus.sunnah.db.contracts.BookContract
import com.alfaazplus.sunnah.db.contracts.ChapterContract
import com.alfaazplus.sunnah.db.contracts.ChapterContract.Columns
import com.alfaazplus.sunnah.db.contracts.CollectionContract

@Entity(
    tableName = ChapterContract.TABLE_NAME,
    primaryKeys = [Columns.ID, Columns.COLLECTION_ID, Columns.BOOK_ID],
    foreignKeys = [
        ForeignKey(
            entity = HBook::class,
            parentColumns = [BookContract.Columns.ID, BookContract.Columns.COLLECTION_ID],
            childColumns = [Columns.BOOK_ID, Columns.COLLECTION_ID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HCollection::class,
            parentColumns = [CollectionContract.Columns.ID],
            childColumns = [Columns.COLLECTION_ID],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = [Columns.BOOK_ID]),
        Index(value = [Columns.BOOK_ID, Columns.COLLECTION_ID]),
        Index(value = [Columns.COLLECTION_ID]),
    ]
)
data class HChapter(
    @ColumnInfo(name = Columns.ID) val id: Double,
    @ColumnInfo(name = Columns.COLLECTION_ID) val collectionId: Int,
    @ColumnInfo(name = Columns.BOOK_ID) val bookId: Int,
    @ColumnInfo(name = Columns.SERIAL_NUMBER) val serialNumber: String,
    @ColumnInfo(name = Columns.TITLE) val title: String,
    @ColumnInfo(name = Columns.INTRO) val intro: String?,
    @ColumnInfo(name = Columns.DESCRIPTION) val description: String?,
    @ColumnInfo(name = Columns.ENDING) val ending: String?,
)
