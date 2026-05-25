@file:Suppress("UNCHECKED_CAST")

package com.alfaazplus.sunnah.ui.utils.reader

import com.alfaazplus.sunnah.ui.utils.app.AppLocale
import com.alfaazplus.sunnah.ui.utils.app.appLocaleFlow
import com.alfaazplus.sunnah.ui.utils.preferences.HadithTextOption
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged


data class ChangeConfig(
    val appLangCode: String,
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
            appLocaleFlow,
            ReaderPreferences.hadithTranslationFlow(),
            ReaderPreferences.hadithTextOptionFlow(),
            ReaderPreferences.isSanadEnabledFlow(),
            ReaderPreferences.textSizePercentArabicFlow(),
            ReaderPreferences.textSizePercentTranslationFlow(),
            ReaderPreferences.isSerifFontStyleFlow(),
        ) { values ->
            ChangeConfig(
                appLangCode = (values[0] as AppLocale).platformLocale.language,
                selectedTranslationLangCode = values[1] as String,
                hadithTextOption = values[2] as HadithTextOption,
                isSanadEnabled = values[3] as Boolean,
                txtSizePercentArabic = values[4] as Int,
                txtSizePercentTranslation = values[5] as Int,
                isSerifFontStyle = values[6] as Boolean,
            )
        }.distinctUntilChanged()
    }
}
