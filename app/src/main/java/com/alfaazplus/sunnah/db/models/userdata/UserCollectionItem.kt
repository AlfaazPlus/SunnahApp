package com.alfaazplus.sunnah.db.models.userdata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.alfaazplus.sunnah.db.contracts.userdata.UserCollectionContract
import com.alfaazplus.sunnah.db.contracts.userdata.UserCollectionItemContract
import com.alfaazplus.sunnah.db.contracts.userdata.UserCollectionItemContract.Columns
import java.util.Date

@Entity(
    tableName = UserCollectionItemContract.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = UserCollection::class,
            parentColumns = [UserCollectionContract.Columns.ID],
            childColumns = [Columns.USER_COLLECTION_ID],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = [Columns.USER_COLLECTION_ID])
    ]
)
data class UserCollectionItem(
    @ColumnInfo(name = Columns.ID) @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = Columns.USER_COLLECTION_ID) val userCollectionId: Long,
    @ColumnInfo(name = Columns.HADITH_COLLECTION_ID) val hadithCollectionId: Int,
    @ColumnInfo(name = Columns.HADITH_BOOK_ID) val hadithBookId: Int,
    @ColumnInfo(name = Columns.HADITH_NUMBER) val hadithNumber: String,
    @ColumnInfo(name = Columns.REMARK) val remark: String,
    @ColumnInfo(name = Columns.CREATED_AT) val createdAt: Date = Date(),
    @ColumnInfo(name = Columns.UPDATED_AT) val updatedAt: Date = Date(),
)
