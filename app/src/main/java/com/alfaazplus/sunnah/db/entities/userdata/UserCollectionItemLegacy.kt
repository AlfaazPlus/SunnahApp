package com.alfaazplus.sunnah.db.entities.userdata

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.alfaazplus.sunnah.db.contracts.userdata.UserCollectionItemContract
import com.alfaazplus.sunnah.db.contracts.userdata.UserCollectionItemContract.Columns
import java.util.Date

@Entity(
    tableName = UserCollectionItemContract.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = UserCollectionLegacy::class,
            parentColumns = ["id"],
            childColumns = [Columns.USER_COLLECTION_ID],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = [Columns.USER_COLLECTION_ID])]
)
@Deprecated("to v2")
data class UserCollectionItemLegacy(
    @ColumnInfo(name = Columns.ID)
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = Columns.USER_COLLECTION_ID)
    val userCollectionId: Long,
    @ColumnInfo(name = Columns.HADITH_COLLECTION_ID)
    val hadithCollectionId: Int,
    @ColumnInfo(name = Columns.HADITH_BOOK_ID)
    val hadithBookId: Int,
    @ColumnInfo(name = Columns.HADITH_NUMBER)
    val hadithNumber: String,
    @ColumnInfo(name = Columns.REMARK)
    val remark: String,
    @ColumnInfo(name = Columns.CREATED_AT)
    val createdAt: Date = Date(),
    @ColumnInfo(name = Columns.UPDATED_AT)
    val updatedAt: Date = Date(),
)
