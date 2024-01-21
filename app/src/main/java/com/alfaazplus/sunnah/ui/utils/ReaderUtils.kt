package com.alfaazplus.sunnah.ui.utils

import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.helpers.HadithTextHelper
import com.alfaazplus.sunnah.ui.utils.keys.Keys

object ReaderUtils {
    const val HADITH_TEXT_OPTION_BOTH = "hadith_text_option_both"
    const val HADITH_TEXT_OPTION_ONLY_ARABIC = "hadith_text_option_arabic"
    const val HADITH_TEXT_OPTION_ONLY_TRANSLATION = "hadith_text_option_translation"

    fun resolveHadithTextOptionLabel(option: String): Int {
        return when (option) {
            HADITH_TEXT_OPTION_ONLY_ARABIC -> R.string.show_only_arabic
            HADITH_TEXT_OPTION_ONLY_TRANSLATION -> R.string.show_only_translation
            else -> R.string.show_arabic_and_translation
        }
    }
}