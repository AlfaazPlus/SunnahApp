package com.alfaazplus.sunnah.db.entities.userdata.v2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "user_bookmarks",
)
data class UserBookmark(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "hadith_id") val hadithId: String,
    @ColumnInfo(name = "remark") val remark: String,
    @ColumnInfo(name = "created_at") val createdAt: Date = Date(),
    @ColumnInfo(name = "updated_at") val updatedAt: Date = Date(),
)
