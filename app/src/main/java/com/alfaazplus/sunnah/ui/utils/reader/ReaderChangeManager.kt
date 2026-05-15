@file:Suppress("UNCHECKED_CAST")

package com.alfaazplus.sunnah.ui.utils.reader

import com.alfaazplus.sunnah.ui.utils.preferences.HadithTextOption
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged


data class ChangeConfig(
    val hadithTextOption: HadithTextOption,
    val isSanadEnabled: Boolean,
    val txtSizePercentArabic: Int,
    val txtSizePercentTranslation: Int,
    val isSerifFontStyle: Boolean,
)

object ReaderChangeManager {
    fun changeFlow(): Flow<ChangeConfig> {
        return combine(
            ReaderPreferences.hadithTextOptionFlow(),
            ReaderPreferences.isSanadEnabledFlow(),
            ReaderPreferences.textSizePercentArabicFlow(),
            ReaderPreferences.textSizePercentTranslationFlow(),
            ReaderPreferences.isSerifFontStyleFlow()
        ) { hadithTextOption, isSanadEnabled, txtSizePercentArabic, txtSizePercentTranslation, isSerifFontStyle ->
            ChangeConfig(
                hadithTextOption = hadithTextOption,
                isSanadEnabled = isSanadEnabled,
                txtSizePercentArabic = txtSizePercentArabic,
                txtSizePercentTranslation = txtSizePercentTranslation,
                isSerifFontStyle = isSerifFontStyle,
            )
        }.distinctUntilChanged()
    }
}
