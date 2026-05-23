package com.alfaazplus.sunnah.db.entities.userdata.v2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "user_collection_items",
    foreignKeys = [
        ForeignKey(
            entity = UserCollection::class,
            parentColumns = ["id"],
            childColumns = ["collection_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["collection_id"])]
)
data class UserCollectionItem(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "collection_id")
    val userCollectionId: Long,
    @ColumnInfo(name = "hadith_id")
    val hadithId: String,
    @ColumnInfo(name = "remark")
    val remark: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date = Date(),
)
