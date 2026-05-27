package com.alfaazplus.sunnah.ui.utils.preferences

import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils.DEFAULT_TRANSLATION
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import com.alfaazplus.sunnah.ui.utils.shared_preference.PrefKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class HadithLayout(val value: String) {
    HORIZONTAL("hadith_layout_horizontal"),
    VERTICAL("hadith_layout_vertical");

    companion object {
        fun fromValue(value: String): HadithLayout {
            return entries.find { it.value == value } ?: VERTICAL
        }
    }
}

enum class HadithTextOption(val value: String) {
    ONLY_ARABIC("hadith_text_option_arabic"),
    ONLY_TRANSLATION("hadith_text_option_translation"),
    BOTH("hadith_text_option_both");

    companion object {
        fun fromValue(value: String): HadithTextOption {
            return entries.find { it.value == value } ?: BOTH
        }
    }
}


object ReaderPreferences {
    val KEY_HADITH_LAYOUT = PrefKey(stringPreferencesKey("hadith_layout"), HadithLayout.VERTICAL.value)
    val KEY_HADITH_TEXT_OPTION = PrefKey(stringPreferencesKey("hadith_text_option"), HadithTextOption.BOTH.value)
    val KEY_TEXT_SIZE_PER_ARABIC = PrefKey(intPreferencesKey("text_size_arabic"), 100)
    val KEY_TEXT_SIZE_PER_TRANSLATION = PrefKey(intPreferencesKey("text_size_translation"), 100)
    val KEY_IS_SANAD_ENABLED = PrefKey(booleanPreferencesKey("show_sanad"), true)
    val KEY_IS_SERIF_FONT_STYLE = PrefKey(booleanPreferencesKey("serif_font_style"), false)
    val KEY_HADITH_TRANSLATION = PrefKey(stringPreferencesKey("hadith_translation"), DEFAULT_TRANSLATION.langCode)

    @Composable
    fun resolveHadithTextOptionLabel(): Int {
        val option = observeHadithTextOption()

        return when (option) {
            HadithTextOption.ONLY_ARABIC -> R.string.show_only_arabic
            HadithTextOption.ONLY_TRANSLATION -> R.string.show_only_translation
            HadithTextOption.BOTH -> R.string.show_arabic_and_translation
        }
    }

    @Composable
    fun resolveHadithLayoutLabel(): Int {
        val option = observeHadithLayout()

        return when (option) {
            HadithLayout.HORIZONTAL -> R.string.horizontal
            HadithLayout.VERTICAL -> R.string.vertical
        }
    }

    @Composable
    fun observeHadithLayout(): HadithLayout {
        return DataStoreManager
            .observe(KEY_HADITH_LAYOUT)
            .let { HadithLayout.fromValue(it) }
    }

    fun hadithLayoutFlow(): Flow<HadithLayout> {
        return DataStoreManager
            .flow(KEY_HADITH_LAYOUT)
            .map { HadithLayout.fromValue(it) }
    }

    suspend fun setHadithLayout(layout: HadithLayout) {
        DataStoreManager.write(KEY_HADITH_LAYOUT, layout.value)
    }

    suspend fun getHadithTextOption(): HadithTextOption {
        return DataStoreManager
            .readFirst(KEY_HADITH_TEXT_OPTION)
            .let { HadithTextOption.fromValue(it) }
    }

    suspend fun setHadithTextOption(option: HadithTextOption) {
        return DataStoreManager.write(KEY_HADITH_TEXT_OPTION, option.value)
    }

    @Composable
    fun observeHadithTextOption(): HadithTextOption {
        return DataStoreManager
            .observe(KEY_HADITH_TEXT_OPTION)
            .let { HadithTextOption.fromValue(it) }
    }

    fun hadithTextOptionFlow(): Flow<HadithTextOption> {
        return DataStoreManager
            .flow(KEY_HADITH_TEXT_OPTION)
            .map {
                HadithTextOption.fromValue(it)
            }
    }

    @Composable
    fun observeTextSizePercentArabic(): Int {
        return DataStoreManager.observe(KEY_TEXT_SIZE_PER_ARABIC)
    }

    fun textSizePercentArabicFlow(): Flow<Int> {
        return DataStoreManager.flow(KEY_TEXT_SIZE_PER_ARABIC)
    }

    @Composable
    fun observeTextSizePercentTranslation(): Int {
        return DataStoreManager.observe(KEY_TEXT_SIZE_PER_TRANSLATION)
    }

    fun textSizePercentTranslationFlow(): Flow<Int> {
        return DataStoreManager.flow(KEY_TEXT_SIZE_PER_TRANSLATION)
    }

    suspend fun getIsSanadEnabled(): Boolean {
        return DataStoreManager.readFirst(KEY_IS_SANAD_ENABLED)
    }

    @Composable
    fun observeIsSanadEnabled(): Boolean {
        return DataStoreManager.observe(KEY_IS_SANAD_ENABLED)
    }

    fun isSanadEnabledFlow(): Flow<Boolean> {
        return DataStoreManager.flow(KEY_IS_SANAD_ENABLED)
    }

    @Composable
    fun observeIsSerifFontStyle(): Boolean {
        return DataStoreManager.observe(KEY_IS_SERIF_FONT_STYLE)
    }

    fun isSerifFontStyleFlow(): Flow<Boolean> {
        return DataStoreManager.flow(KEY_IS_SERIF_FONT_STYLE)
    }

    suspend fun getIsSerifFontStyle(): Boolean {
        return DataStoreManager.readFirst(KEY_IS_SERIF_FONT_STYLE)
    }

    suspend fun getHadithTranslation(): String {
        return DataStoreManager.readFirst(KEY_HADITH_TRANSLATION)
    }

    suspend fun setHadithTranslation(id: String) {
        return DataStoreManager.write(
            KEY_HADITH_TRANSLATION,
            id,
        )
    }

    @Composable
    fun observeHadithTranslation(): String {
        return DataStoreManager.observe(KEY_HADITH_TRANSLATION)
    }

    fun hadithTranslationFlow(): Flow<String> {
        return DataStoreManager.flow(KEY_HADITH_TRANSLATION)
    }
}
