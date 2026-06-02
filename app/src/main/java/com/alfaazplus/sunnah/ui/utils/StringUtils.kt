package com.alfaazplus.sunnah.ui.utils

import java.util.Locale

object StringUtils {
    const val DASH: String = "–"
    const val VERTICAL_BAR: String = "│"
    const val HYPHEN: String = "—"
    const val RTL_MARK: String = "\u200F"
    private const val LTR_ISOLATE_START = "\u2066"
    private const val LTR_ISOLATE_END = "\u2069"
    const val ELLIPSIS: String = "…"

    /**
     * Formats a collection name and hadith number for display, applying bidi isolates
     * so mixed RTL labels and LTR references (e.g. "1551a") render in reading order.
     */
    fun formatCollectionNumbering(
        collectionName: String?,
        number: String?,
        langCode: String?,
    ): String {
        val resolvedName = collectionName ?: "?"
        val resolvedNumber = number ?: "?"

        val displayNumber = if (langCode == "bn") resolvedNumber.toBengaliDigits()
        else resolvedNumber

        return if (isRtlLanguage(langCode)) {
            "$resolvedName : $LTR_ISOLATE_START$displayNumber$LTR_ISOLATE_END"
        } else {
            "$resolvedName: $displayNumber"
        }
    }

    fun formatNumbering(
        number: String?,
        langCode: String?,
    ): String {
        if (number.isNullOrEmpty()) return ""

        return if (langCode == "bn") number.toBengaliDigits()
        else number
    }

    private fun String.toBengaliDigits(): String = map { ch ->
        when (ch) {
            '0' -> '০'
            '1' -> '১'
            '2' -> '২'
            '3' -> '৩'
            '4' -> '৪'
            '5' -> '৫'
            '6' -> '৬'
            '7' -> '৭'
            '8' -> '৮'
            '9' -> '৯'
            else -> ch
        }
    }.joinToString("")

    /**
     * Title case a text.
     * 
     * @param text Text to be cased.
     * @return Returns title cased text.
     */
    fun toTitleCase(text: String?): String? {
        if (text.isNullOrEmpty()) return null

        return text[0]
            .titlecaseChar()
            .toString() + text
            .substring(1)
            .lowercase(Locale.getDefault())
    }


    fun isRTL(c: Char): Boolean {
        val d = Character.getDirectionality(c)
        return d == Character.DIRECTIONALITY_RIGHT_TO_LEFT || d == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC || d == Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING || d == Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE
    }

    fun isRtlLanguage(langCode: String?): Boolean {
        if (langCode.isNullOrEmpty()) {
            return false
        }

        val rtlLangCodes = arrayOf("ar", "fa", "ur", "ps", "sd", "ug", "dv", "he", "yi", "ku", "syr", "az-Arab", "ckb")

        val parts: Array<String?> = langCode
            .split("[_-]".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()

        if (parts.isEmpty()) {
            return false
        }

        val code = parts[0]

        for (rtlLang in rtlLangCodes) {
            if (rtlLang.equals(code, ignoreCase = true)) {
                return true
            }
        }

        return false
    }
}
