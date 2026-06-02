package com.alfaazplus.sunnah.db.relations

import androidx.room3.Ignore

data class HadithNavigationItem(
    val hadithId: String,
    val bookId: String,
    val number: String?,
) {
    @Ignore
    var visibleNumbering: String = ""
}
