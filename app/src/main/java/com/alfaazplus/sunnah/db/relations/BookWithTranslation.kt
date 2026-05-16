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

    fun getTitle(): String? {
        return sequenceOf("en", "ar").firstNotNullOfOrNull {
            getTranslation(it)?.title?.takeIf { it.isNotEmpty() }
        }
    }

    fun getTitle(langCode: String = "en"): String? {
        return getTranslation(langCode)?.title?.takeIf { it.isNotEmpty() }
    }

    fun getTitlePair(): String {
        val arTitle = getTitle("ar")
        val enTitle = getTitle("en") ?: return arTitle ?: ""

        return "$enTitle (${arTitle ?: ""})"
    }

    fun getIntro(langCode: String = "en"): String? {
        return getTranslation(langCode)?.intro?.takeIf { it.isNotEmpty() }
    }

    fun getNotes(langCode: String = "en"): String? {
        return getTranslation(langCode)?.notes?.takeIf { it.isNotEmpty() }
    }

    fun getPreamble(langCode: String = "en"): String? {
        return getTranslation(langCode)?.preamble?.takeIf { it.isNotEmpty() }
    }
}
