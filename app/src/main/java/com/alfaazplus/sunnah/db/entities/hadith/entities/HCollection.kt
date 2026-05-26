package com.alfaazplus.sunnah.db.entities.hadith.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.alfaazplus.sunnah.db.contracts.CollectionContract
import com.alfaazplus.sunnah.db.contracts.CollectionContract.Columns

@Deprecated("v2")
@Entity(tableName = CollectionContract.TABLE_NAME)
data class HCollection(
    @ColumnInfo(name = Columns.ID) @PrimaryKey val id: Int,
    @ColumnInfo(name = Columns.TYPE) val type: String,
    @ColumnInfo(name = Columns.HAS_VOLUMES) val hasVolumes: Boolean,
    @ColumnInfo(name = Columns.HAS_BOOKS) val hasBooks: Boolean,
    @ColumnInfo(name = Columns.HAS_CHAPTERS) val hasChapters: Boolean,
    @ColumnInfo(name = Columns.NAME) val name: String,
    @ColumnInfo(name = Columns.INTRO) val intro: String?,
    @ColumnInfo(name = Columns.DESCRIPTION) val description: String?,
    @ColumnInfo(name = Columns.NUMBERING_SOURCE) val numberingSource: String?,
)
