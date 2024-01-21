package com.alfaazplus.sunnah.ui.models

import com.alfaazplus.sunnah.db.models.hadith.entities.Hadith
import com.alfaazplus.sunnah.db.models.hadith.entities.HadithTranslation

data class HadithWithTranslation(
    val hadith: Hadith,
    val translation: HadithTranslation?,
)