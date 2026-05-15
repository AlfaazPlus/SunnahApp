package com.alfaazplus.sunnah.ui.models

import androidx.compose.ui.text.AnnotatedString
import com.alfaazplus.sunnah.db.entities.hadith.HadithChapter

@Deprecated("v2")
data class ParsedChapter(private val hc: HadithChapter) {
    var chapterIntro: AnnotatedString? = null
    var chapterIntroEn: AnnotatedString? = null

    val chapter get() = hc.chapter
    val info get() = hc.info
}
