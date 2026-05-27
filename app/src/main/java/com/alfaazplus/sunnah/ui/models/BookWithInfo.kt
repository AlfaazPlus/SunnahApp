package com.alfaazplus.sunnah.ui.models

import com.alfaazplus.sunnah.db.entities.hadith.entities.HBook
import com.alfaazplus.sunnah.db.entities.hadith.entities.HBookInfo

@Deprecated("v2")
data class BookWithInfo(
    val book: HBook,
    val info: HBookInfo?,
)
