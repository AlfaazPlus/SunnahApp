package com.alfaazplus.sunnah.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.alfaazplus.sunnah.db.entities.v2.ChapterEntity
import com.alfaazplus.sunnah.db.entities.v2.ChapterTranslationEntity

data class ChapterWithTranslation(
    @Embedded
    val chapter: ChapterEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "chapter_id",
    )
    val translations: List<ChapterTranslationEntity>,
)
