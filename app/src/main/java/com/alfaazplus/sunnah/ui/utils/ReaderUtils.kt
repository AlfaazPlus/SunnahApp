package com.alfaazplus.sunnah.ui.utils

import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager

object ReaderUtils {
    const val HADITH_LAYOUT_HORIZONTAL = "hadith_layout_horizontal"
    const val HADITH_LAYOUT_VERTICAL = "hadith_layout_vertical"

    const val HADITH_TEXT_OPTION_BOTH = "hadith_text_option_both"
    const val HADITH_TEXT_OPTION_ONLY_ARABIC = "hadith_text_option_arabic"
    const val HADITH_TEXT_OPTION_ONLY_TRANSLATION = "hadith_text_option_translation"

    @Composable
    fun resolveHadithTextOptionLabel(): Int {
        val option = getHadithTextOption()

        return when (option) {
            HADITH_TEXT_OPTION_ONLY_ARABIC -> R.string.show_only_arabic
            HADITH_TEXT_OPTION_ONLY_TRANSLATION -> R.string.show_only_translation
            else -> R.string.show_arabic_and_translation
        }
    }

    @Composable
    fun resolveHadithLayoutLabel(): Int {
        val option = getHadithLayoutOption()

        return when (option) {
            HADITH_LAYOUT_HORIZONTAL -> R.string.horizontal
            HADITH_LAYOUT_VERTICAL -> R.string.vertical
            else -> R.string.horizontal
        }
    }

    @Composable
    fun getHadithLayoutOption(): String {
        return DataStoreManager.observe(stringPreferencesKey(Keys.HADITH_LAYOUT), HADITH_LAYOUT_HORIZONTAL)
    }

    @Composable
    fun getHadithTextOption(): String {
        return DataStoreManager.observe(stringPreferencesKey(Keys.HADITH_TEXT_OPTION), HADITH_TEXT_OPTION_BOTH)
    }

    @Composable
    fun getIsSanadEnabled(): Boolean {
        return DataStoreManager.observe(booleanPreferencesKey(Keys.SHOW_SANAD), true)
    }
}