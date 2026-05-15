package com.alfaazplus.sunnah.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.alfaazplus.sunnah.db.entities.v2.BookEntity
import com.alfaazplus.sunnah.db.entities.v2.BookTranslationEntity

data class BookWithTranslation(
    @Embedded
    val book: BookEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "book_id",
    )
    val translations: List<BookTranslationEntity>,
)
