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
) {
    fun getTranslation(langCode: String): ChapterTranslationEntity? {
        return translations.firstOrNull { it.lang == langCode }
    }

    fun getTitle(langCode: String): String? {
        return getTranslation(langCode)?.title?.takeIf { it.isNotEmpty() }
    }

    fun getIntro(langCode: String): String? {
        return getTranslation(langCode)?.intro?.takeIf { it.isNotEmpty() }
    }
}
