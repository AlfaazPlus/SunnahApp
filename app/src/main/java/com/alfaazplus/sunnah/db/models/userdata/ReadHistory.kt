package com.alfaazplus.sunnah.db.models.userdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.alfaazplus.sunnah.db.contracts.userdata.ReadHistoryContract
import com.alfaazplus.sunnah.db.contracts.userdata.ReadHistoryContract.Columns
import java.util.Date

@Entity(
    tableName = ReadHistoryContract.TABLE_NAME,
    primaryKeys = [
        Columns.HADITH_COLLECTION_ID,
        Columns.HADITH_BOOK_ID,
        Columns.HADITH_NUMBER,
    ]
)
data class ReadHistory(
    @ColumnInfo(name = Columns.HADITH_COLLECTION_ID) val hadithCollectionId: Int,
    @ColumnInfo(name = Columns.HADITH_BOOK_ID) val hadithBookId: Int,
    @ColumnInfo(name = Columns.HADITH_NUMBER) val hadithNumber: String,
    @ColumnInfo(name = Columns.CREATED_AT) val createdAt: Date = Date(),
) {
    fun key(): String {
        return "$hadithCollectionId-$hadithBookId-$hadithNumber"
    }
}
