package com.alfaazplus.sunnah.ui.utils.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.intPreferencesKey
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager

@Composable
fun getArabicTextSize(): Pair<TextUnit, TextUnit> {
    val arabicTextSizePercentage = DataStoreManager.observe(intPreferencesKey(Keys.TEXT_SIZE_ARABIC), 100)

    val textSize = (arabicTextSizePercentage / 100f * 20f).toInt().sp
    val lineHeight = (arabicTextSizePercentage / 100f * 32f).toInt().sp

    return textSize to lineHeight
}

@Composable
fun getTranslationTextSize()
: Pair<TextUnit, TextUnit> {
    val translationTextSizePercentage = DataStoreManager.observe(intPreferencesKey(Keys.TEXT_SIZE_TRANSLATION), 100)

    val textSize = (translationTextSizePercentage / 100f * 20f).toInt().sp
    val lineHeight = (translationTextSizePercentage / 100f * 28f).toInt().sp

    return textSize to lineHeight
}