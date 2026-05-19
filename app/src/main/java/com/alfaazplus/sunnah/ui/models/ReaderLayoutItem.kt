package com.alfaazplus.sunnah.ui.models

import androidx.compose.ui.text.AnnotatedString
import com.alfaazplus.sunnah.db.interfaces.HadithMethods
import com.alfaazplus.sunnah.db.relations.ChapterWithTranslation
import com.alfaazplus.sunnah.db.relations.HadithWithContents
import com.alfaazplus.sunnah.helpers.HadithGradeText

data class HadithChapterUi(
    val chapter: ChapterWithTranslation,
    val titles: List<AnnotatedString>,
    val intros: List<AnnotatedString>,
)

sealed class ReaderLayoutItem {
    abstract val key: String

    data class HadithUI(
        val hwc: HadithWithContents,
        val chapterUi: HadithChapterUi?,
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
