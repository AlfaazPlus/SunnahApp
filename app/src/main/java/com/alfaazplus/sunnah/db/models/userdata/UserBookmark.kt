package com.alfaazplus.sunnah.db.models.userdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alfaazplus.sunnah.db.contracts.userdata.UserBookmarkContract
import com.alfaazplus.sunnah.db.contracts.userdata.UserBookmarkContract.Columns
import java.util.Date

@Entity(
    tableName = UserBookmarkContract.TABLE_NAME,
)
data class UserBookmark(
    @ColumnInfo(name = Columns.ID) @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = Columns.HADITH_COLLECTION_ID) val hadithCollectionId: Int,
    @ColumnInfo(name = Columns.HADITH_BOOK_ID) val hadithBookId: Int,
    @ColumnInfo(name = Columns.HADITH_NUMBER) val hadithNumber: Int,
    @ColumnInfo(name = Columns.REMARK) val remark: String,
    @ColumnInfo(name = Columns.CREATED_AT) val createdAt: Date = Date(),
    @ColumnInfo(name = Columns.UPDATED_AT) val updatedAt: Date = Date(),
)
