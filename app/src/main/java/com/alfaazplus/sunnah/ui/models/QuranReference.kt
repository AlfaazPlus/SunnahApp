package com.alfaazplus.sunnah.ui.models

sealed class QuranReference {
    abstract val chapterNo: Int

    /**
     * e.g., 2:255
     */
    data class Single(
        override val chapterNo: Int,
        val verseNo: Int,
    ) : QuranReference()

    /**
     * e.g., 2:285-286
     */
    data class Range(
        override val chapterNo: Int,
        val fromVerseNo: Int,
        val toVerseNo: Int,
    ) : QuranReference()

    /**
     * e.g., 2:12,13,17
     */
    data class Discrete(
        override val chapterNo: Int,
        val verses: Set<Int>,
    ) : QuranReference()

    companion object {
        fun parse(chapterNo: Int, verses: String): QuranReference? {
            if (chapterNo <= 0) return null

            val trimmed = verses.trim()
            if (trimmed.isEmpty()) return null

            if (trimmed.contains(',')) {
                return parseDiscrete(chapterNo, trimmed)
            }

            if (trimmed.contains('-')) {
                return parseRange(chapterNo, trimmed)
            }

            val verseNo = trimmed.toPositiveInt() ?: return null

            return Single(chapterNo, verseNo)
        }

        private fun parseRange(chapterNo: Int, trimmed: String): QuranReference? {
            val parts = trimmed.split('-', limit = 2)

            if (parts.size != 2) return null

            val fromVerse = parts[0]
                .trim()
                .toPositiveInt() ?: return null

            val toVerse = parts[1]
                .trim()
                .toPositiveInt() ?: return null

            if (fromVerse > toVerse) return null

            return if (fromVerse == toVerse) {
                Single(chapterNo, fromVerse)
            } else {
                Range(chapterNo, fromVerse, toVerse)
            }
        }

        private fun parseDiscrete(chapterNo: Int, trimmed: String): QuranReference? {
            val tokens = trimmed
                .split(',')
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            
            if (tokens.isEmpty()) return null

            val verseNos = tokens.map { token -> token.toPositiveInt() ?: return null }

            return when (verseNos.size) {
                1 -> Single(chapterNo, verseNos.first())
                else -> Discrete(chapterNo, verseNos.toSet())
            }
        }

        private fun String.toPositiveInt(): Int? = toIntOrNull()?.takeIf { it > 0 }
    }
}
