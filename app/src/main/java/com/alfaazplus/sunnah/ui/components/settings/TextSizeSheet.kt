package com.alfaazplus.sunnah.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.SwitchItem
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.utils.ReaderUtils
import com.alfaazplus.sunnah.ui.utils.composable.getArabicTextSize
import com.alfaazplus.sunnah.ui.utils.composable.getTranslationTextSize
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import kotlinx.coroutines.launch

@Composable
fun HadithTextPreview(
    isArabic: Boolean,
    previewText: String,
    isSerif: Boolean,
) {
    val (fontSize, fontLineHeight) = if (isArabic) {
        getArabicTextSize()
    } else {
        getTranslationTextSize()
    }

    Text(
        previewText,
        fontSize = fontSize,
        lineHeight = fontLineHeight,
        fontFamily = if (isArabic) fontUthmani else if (isSerif) FontFamily.Serif else FontFamily.SansSerif,
    )
}

@Composable
private fun TextSizeSlider(key: String, title: Int, previewText: String, isSerif: Boolean = false) {
    val coroutineScope = rememberCoroutineScope()
    val textSize = DataStoreManager.observe(intPreferencesKey(key), 100)

    val min = 50f
    val max = 200f
    val steps = max.toInt() - min.toInt()


    ListItemCategoryLabel(stringResource(title))
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Slider(
            modifier = Modifier.weight(1f), value = textSize.toFloat(), onValueChange = {
                coroutineScope.launch {
                    DataStoreManager.write(intPreferencesKey(key), it.toInt())
                }
            }, valueRange = min..max, steps = steps
        )
        Text(
            text = "${textSize}%", modifier = Modifier.padding(start = 10.dp), style = MaterialTheme.typography.labelSmall
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides if (key == Keys.TEXT_SIZE_ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            HadithTextPreview(key == Keys.TEXT_SIZE_ARABIC, previewText, isSerif)
        }
    }
}

@Composable
fun TextSizeSheet(isOpen: Boolean, onDismiss: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val isSerifFontStyle = ReaderUtils.getIsSerifFontStyle()

    BottomSheet(
        isOpen = isOpen,
        onDismiss = onDismiss,
        icon = R.drawable.ic_text_size,
        title = stringResource(R.string.text_size_and_style),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            TextSizeSlider(
                Keys.TEXT_SIZE_ARABIC,
                R.string.arabic_text_size,
                previewText = " مَنْ صَامَ رَمَضَانَ إِيمَانًا وَاحْتِسَابًا غُفِرَ لَهُ مَا تَقَدَّمَ مِنْ ذَنْبِهِ"
            )
            TextSizeSlider(
                Keys.TEXT_SIZE_TRANSLATION,
                R.string.translation_text_size,
                previewText = "Whoever fasts Ramadan out of faith and in the hope of reward, he will be forgiven his previous sins.",
                isSerifFontStyle
            )
            SwitchItem(
                modifier = Modifier.padding(top = 16.dp),
                title = R.string.serif_font_style,
                checked = isSerifFontStyle,
            ) {
                coroutineScope.launch {
                    DataStoreManager.write(booleanPreferencesKey(Keys.SERIF_FONT_STYLE), it)
                }
            }
        }
    }
}