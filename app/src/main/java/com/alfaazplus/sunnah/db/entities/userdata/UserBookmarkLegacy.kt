package com.alfaazplus.sunnah.db.entities.userdata

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.alfaazplus.sunnah.db.contracts.userdata.UserBookmarkContract
import com.alfaazplus.sunnah.db.contracts.userdata.UserBookmarkContract.Columns
import java.util.Date

@Entity(
    tableName = UserBookmarkContract.TABLE_NAME,
)
@Deprecated("")
data class UserBookmarkLegacy(
    @ColumnInfo(name = Columns.ID) @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = Columns.HADITH_COLLECTION_ID) val hadithCollectionId: Int,
    @ColumnInfo(name = Columns.HADITH_BOOK_ID) val hadithBookId: Int,
    @ColumnInfo(name = Columns.HADITH_NUMBER) val hadithNumber: String,
    @ColumnInfo(name = Columns.REMARK) val remark: String,
    @ColumnInfo(name = Columns.CREATED_AT) val createdAt: Date = Date(),
    @ColumnInfo(name = Columns.UPDATED_AT) val updatedAt: Date = Date(),
)
