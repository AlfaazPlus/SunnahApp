package com.alfaazplus.sunnah.ui.utils

import java.util.Locale

object StringUtils {
    const val DASH: String = "–"
    const val VERTICAL_BAR: String = "│"
    const val HYPHEN: String = "—"
    const val RTL_MARK: String = "\u200F"
    const val ELLIPSIS: String = "…"


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
