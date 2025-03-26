package com.alfaazplus.sunnah.db.models.hadith.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.alfaazplus.sunnah.db.contracts.BookContract
import com.alfaazplus.sunnah.db.contracts.ChapterContract
import com.alfaazplus.sunnah.db.contracts.CollectionContract
import com.alfaazplus.sunnah.db.contracts.HadithContract
import com.alfaazplus.sunnah.db.contracts.HadithContract.Columns

@Entity(
    tableName = HadithContract.TABLE_NAME,
    indices = [
        Index(value = [Columns.URN], unique = true),
        Index(value = [Columns.CHAPTER_ID]),
        Index(value = [Columns.BOOK_ID, Columns.COLLECTION_ID]),
        Index(value = [Columns.COLLECTION_ID, Columns.BOOK_ID, Columns.CHAPTER_ID]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = HCollection::class,
            parentColumns = [CollectionContract.Columns.ID],
            childColumns = [Columns.COLLECTION_ID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HBook::class,
            parentColumns = [BookContract.Columns.ID, BookContract.Columns.COLLECTION_ID],
            childColumns = [Columns.BOOK_ID, Columns.COLLECTION_ID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = HChapter::class,
            parentColumns = [ChapterContract.Columns.COLLECTION_ID, ChapterContract.Columns.BOOK_ID, ChapterContract.Columns.ID],
            childColumns = [Columns.COLLECTION_ID, Columns.BOOK_ID, Columns.CHAPTER_ID],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Hadith(
    @ColumnInfo(name = Columns.ID) @PrimaryKey val id: Int,
    @ColumnInfo(name = Columns.URN) val urn: String,
    @ColumnInfo(name = Columns.COLLECTION_ID) val collectionId: Int,
    @ColumnInfo(name = Columns.BOOK_ID) val bookId: Int,
    @ColumnInfo(name = Columns.CHAPTER_ID) val chapterId: Double?,
    @ColumnInfo(name = Columns.HADITH_NUMBER) val hadithNumber: String,
    @ColumnInfo(name = Columns.ORDER_IN_BOOK) val orderInBook: Int,
    @ColumnInfo(name = Columns.HADITH_PREFIX) val hadithPrefix: String?,
    @ColumnInfo(name = Columns.HADITH_TEXT) val hadithText: String,
    @ColumnInfo(name = Columns.HADITH_SUFFIX) val hadithSuffix: String?,
    @ColumnInfo(name = Columns.COMMENTS) val comments: String?,
    @ColumnInfo(name = Columns.GRADES) val grades: String?,
    @ColumnInfo(name = Columns.GRADED_BY) val gradedBy: String?,
    @ColumnInfo(name = Columns.NARRATORS) val narrators: String?,
    @ColumnInfo(name = Columns.NARRATORS2) val narrators2: String?,
    @ColumnInfo(name = Columns.RELATED) val related: String?,
)