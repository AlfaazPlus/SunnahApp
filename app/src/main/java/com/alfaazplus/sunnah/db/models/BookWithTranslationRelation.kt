package com.alfaazplus.sunnah.db.models

import androidx.room.Embedded
import androidx.room.Relation
import com.alfaazplus.sunnah.db.contracts.BookContract
import com.alfaazplus.sunnah.db.contracts.BookInfoContract
import com.alfaazplus.sunnah.db.contracts.HadithContract
import com.alfaazplus.sunnah.db.contracts.HadithTranslationContract
import com.alfaazplus.sunnah.db.models.hadith.entities.HBook
import com.alfaazplus.sunnah.ui.models.BookWithInfo

data class BookWithTranslationRelation(
    @Embedded val book: HBook,
    @Relation(
        parentColumn = BookContract.Columns.ID,
        entityColumn = BookInfoContract.Columns.BOOK_ID,
    ) val info: BookWithInfo,
)
