package com.alfaazplus.sunnah.ui.models

import androidx.compose.ui.text.AnnotatedString
import com.alfaazplus.sunnah.db.models.hadith.HadithChapter

data class ParsedChapter(private val hc: HadithChapter) {
    var chapterIntro: AnnotatedString? = null
    var chapterIntroEn: AnnotatedString? = null

    val chapter get() = hc.chapter
    val info get() = hc.info
}