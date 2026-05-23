package com.alfaazplus.sunnah.repository.userdata

import androidx.compose.ui.text.AnnotatedString
import com.alfaazplus.sunnah.db.entities.v2.HadithBlockType
import com.alfaazplus.sunnah.db.relations.HadithWithContents

internal fun HadithWithContents.plainTranslationText(lang: String = "en"): String? {
    val content = contents.firstOrNull { it.lang == lang } ?: return null

    return content.blocks
        .asSequence()
        .filter {
            it.type == HadithBlockType.MATN ||
                it.type == HadithBlockType.TRANSLATION ||
                it.type == HadithBlockType.NARRATOR
        }
        .mapNotNull { it.text?.trim() }
        .filter { it.isNotEmpty() }
        .joinToString(" ")
        .takeIf { it.isNotEmpty() }
}

internal fun HadithWithContents.displayNumber(fallback: String = hadithId): String {
    return hadith.number?.takeIf { it.isNotBlank() } ?: fallback
}
