@file:Suppress("UNCHECKED_CAST")

package com.alfaazplus.sunnah.ui.utils.reader

import com.alfaazplus.sunnah.ui.utils.preferences.HadithTextOption
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged


data class ChangeConfig(
    val selectedTranslationLangCode: String,
    val hadithTextOption: HadithTextOption,
    val isSanadEnabled: Boolean,
    val txtSizePercentArabic: Int,
    val txtSizePercentTranslation: Int,
    val isSerifFontStyle: Boolean,
)

object ReaderChangeManager {
    fun changeFlow(): Flow<ChangeConfig> {
        return combine<Any, ChangeConfig>(
            ReaderPreferences.hadithTranslationFlow(),
            ReaderPreferences.hadithTextOptionFlow(),
            ReaderPreferences.isSanadEnabledFlow(),
            ReaderPreferences.textSizePercentArabicFlow(),
            ReaderPreferences.textSizePercentTranslationFlow(),
            ReaderPreferences.isSerifFontStyleFlow(),
        ) { values ->
            ChangeConfig(
                selectedTranslationLangCode = values[0] as String,
                hadithTextOption = values[1] as HadithTextOption,
                isSanadEnabled = values[2] as Boolean,
                txtSizePercentArabic = values[3] as Int,
                txtSizePercentTranslation = values[4] as Int,
                isSerifFontStyle = values[5] as Boolean,
            )
        }.distinctUntilChanged()
    }
}
