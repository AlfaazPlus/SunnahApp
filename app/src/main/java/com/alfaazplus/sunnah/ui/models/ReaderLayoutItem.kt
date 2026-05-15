package com.alfaazplus.sunnah.ui.models

import androidx.compose.ui.text.AnnotatedString
import com.alfaazplus.sunnah.db.interfaces.HadithMethods
import com.alfaazplus.sunnah.db.relations.ChapterWithTranslation
import com.alfaazplus.sunnah.db.relations.HadithWithContents
import com.alfaazplus.sunnah.helpers.HadithGradeText

sealed class ReaderLayoutItem {
    abstract val key: String

    data class Chapter(val cwt: ChapterWithTranslation, override val key: String) : ReaderLayoutItem()

    data class HadithUI(
        val hwc: HadithWithContents,
        // based on user preferences
        val parsedArabicText: AnnotatedString?,
        val parsedTranslationText: AnnotatedString?,
        val hasNarratorsChain: Boolean,
        val visibleNumbering: String,
        val gradeText: HadithGradeText?,
        val showDivider: Boolean = true,
        override val key: String,
    ) : ReaderLayoutItem(), HadithMethods by hwc
}
