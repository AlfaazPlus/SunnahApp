package com.alfaazplus.sunnah.db.relations

import androidx.room.ColumnInfo
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

    @ColumnInfo(name = "hadith_count")
    val hadithCount: Int? = null,
) {
    fun getTranslation(langCode: String): BookTranslationEntity? {
        return translations.firstOrNull { it.lang == langCode }
    }

    fun getTitle(langCode: String): String? {
        return getTranslation(langCode)?.title?.takeIf { it.isNotEmpty() }
    }

    fun getTitlePair(langCode: String): String {
        val translatedTitle = getTitle(langCode) ?: return ""
        val arTitle = getTitle("ar")

        return if (arTitle != null) "$translatedTitle ($arTitle)" else translatedTitle
    }

    fun getIntro(langCode: String): String? {
        return getTranslation(langCode)?.intro?.takeIf { it.isNotEmpty() }
    }

    fun getNotes(langCode: String): String? {
        return getTranslation(langCode)?.notes?.takeIf { it.isNotEmpty() }
    }

    fun getPreamble(langCode: String): String? {
        return getTranslation(langCode)?.preamble?.takeIf { it.isNotEmpty() }
    }
}
