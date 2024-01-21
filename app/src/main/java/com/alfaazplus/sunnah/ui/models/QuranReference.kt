package com.alfaazplus.sunnah.ui.models

/**
 * FromVerse and ToVerse are same if reference is to a single verse
 */
data class QuranReference(
    val chapter: Int,
    val fromVerse: Int,
    val toVerse: Int,
) {
    fun isSingleVerse() = fromVerse == toVerse
}