package com.alfaazplus.sunnah.db.relations

data class HadithNavigationItem(
    val hadithId: String,
    val bookId: String,
    val number: String?,
) {
    val visibleNumbering: String
        get() = number ?: hadithId
}
