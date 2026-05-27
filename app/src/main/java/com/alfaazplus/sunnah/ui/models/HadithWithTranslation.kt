package com.alfaazplus.sunnah.ui.models

import com.alfaazplus.sunnah.db.entities.hadith.HadithChapter
import com.alfaazplus.sunnah.db.entities.hadith.entities.Hadith
import com.alfaazplus.sunnah.db.entities.hadith.entities.HadithTranslation

@Deprecated("v2")
data class HadithWithTranslation(
    val hadith: Hadith,
    val translation: HadithTranslation?,
    val chapter: HadithChapter? = null,
)
