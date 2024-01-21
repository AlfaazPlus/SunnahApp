package com.alfaazplus.sunnah.db.models.hadith.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.alfaazplus.sunnah.db.contracts.BookContract
import com.alfaazplus.sunnah.db.contracts.ChapterContract
import com.alfaazplus.sunnah.db.contracts.ChapterInfoContract
import com.alfaazplus.sunnah.db.contracts.ChapterInfoContract.Columns
import com.alfaazplus.sunnah.db.contracts.CollectionContract

@Entity(
    tableName = ChapterInfoContract.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = HChapter::class,
            parentColumns = [ChapterContract.Columns.ID, ChapterContract.Columns.COLLECTION_ID, ChapterContract.Columns.BOOK_ID],
            childColumns = [Columns.CHAPTER_ID, Columns.COLLECTION_ID, Columns.BOOK_ID],
            onDelete = ForeignKey.CASCADE
        ),
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
        Index(value = [Columns.CHAPTER_ID]),
        Index(value = [Columns.CHAPTER_ID, Columns.COLLECTION_ID, Columns.BOOK_ID]),
        Index(value = [Columns.BOOK_ID]),
        Index(value = [Columns.BOOK_ID, Columns.COLLECTION_ID]),
        Index(value = [Columns.COLLECTION_ID]),
    ]
)
data class HChapterInfo(
    @ColumnInfo(name = Columns.ID) @PrimaryKey val id: Int,
    @ColumnInfo(name = Columns.COLLECTION_ID) val collectionId: Int,
    @ColumnInfo(name = Columns.BOOK_ID) val bookId: Int,
    @ColumnInfo(name = Columns.CHAPTER_ID) val chapterId: Double,
    @ColumnInfo(name = Columns.SERIAL_NUMBER) val serialNumber: String,
    @ColumnInfo(name = Columns.TITLE) val title: String,
    @ColumnInfo(name = Columns.INTRO) val intro: String?,
    @ColumnInfo(name = Columns.DESCRIPTION) val description: String?,
    @ColumnInfo(name = Columns.ENDING) val ending: String?,
    @ColumnInfo(name = Columns.LANGUAGE_CODE) val languageCode: String,
)
