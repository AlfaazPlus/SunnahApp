package com.alfaazplus.sunnah.ui.models

import androidx.compose.ui.text.AnnotatedString

data class ParsedHadith(private val hwt: HadithWithTranslation) {
    var hadithText: AnnotatedString? = null
    var narratorPrefixText: CharSequence? = null
    var narratorSuffixText: CharSequence? = null
    var translationNarrator: AnnotatedString? = null
    var translationText: AnnotatedString? = null
    var gradeType: Pair<Int, String>? = null
    var chapter: ParsedChapter? = null

    val hadith get() = hwt.hadith
    val translation get() = hwt.translation
}