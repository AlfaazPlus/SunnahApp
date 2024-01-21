package com.alfaazplus.sunnah.ui.models

import com.alfaazplus.sunnah.db.models.hadith.entities.HBook
import com.alfaazplus.sunnah.db.models.hadith.entities.HBookInfo

data class BookWithInfo(
    val book: HBook,
    val info: HBookInfo?,
)