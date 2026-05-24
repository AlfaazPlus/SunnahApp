package com.alfaazplus.sunnah.db.relations

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.alfaazplus.sunnah.db.entities.v2.BookEntity

data class BookWithHadithCount(
    @Embedded
    val book: BookEntity,
    @ColumnInfo(name = "hadith_count")
    val hadithCount: Int? = null,
)
