package com.alfaazplus.sunnah.db.relations

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.alfaazplus.sunnah.db.entities.v2.CollectionEntity
import com.alfaazplus.sunnah.db.entities.v2.CollectionTranslationEntity

data class CollectionWithTranslation(
    @Embedded
    val collection: CollectionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "collection_id",
    )
    val translations: List<CollectionTranslationEntity>,
) {
    @Ignore
    var isDownloaded = true

    fun getTranslation(langCode: String): CollectionTranslationEntity? {
        return translations.firstOrNull { it.lang == langCode }
    }

    fun getTitleForNumbering(): String? {
        return sequenceOf("en", "ar").firstNotNullOfOrNull {
            getTranslation(it)?.title?.takeIf { it.isNotEmpty() }
        }
    }

    fun getTitle(langCode: String = "en"): String? {
        return getTranslation(langCode)?.title?.takeIf { it.isNotEmpty() }
    }

    fun getTitlePair(langCode: String = "en"): String {
        val arTitle = getTitle("ar")
        val translatedTitle = getTitle(langCode) ?: return arTitle ?: ""

        return "$translatedTitle (${arTitle ?: ""})"
    }

    fun getIntro(langCode: String = "en"): String? {
        return getTranslation(langCode)?.intro?.takeIf { it.isNotEmpty() }
    }

    fun getDescription(langCode: String = "en"): String? {
        return getTranslation(langCode)?.description?.takeIf { it.isNotEmpty() }
    }
}
