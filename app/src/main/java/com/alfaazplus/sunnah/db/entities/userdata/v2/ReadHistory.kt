package com.alfaazplus.sunnah.db.entities.userdata.v2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "read_history",
)
data class ReadHistory(
    @PrimaryKey
    @ColumnInfo(name = "hadith_id")
    val hadithId: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
)
