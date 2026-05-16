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

    fun getDescription(langCode: String = "en"): String? {
        return getTranslation(langCode)?.description?.takeIf { it.isNotEmpty() }
    }
}
