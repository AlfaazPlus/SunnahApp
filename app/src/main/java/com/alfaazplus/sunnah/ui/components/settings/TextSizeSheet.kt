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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.SwitchItem
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences.KEY_IS_SERIF_FONT_STYLE
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences.KEY_TEXT_SIZE_PER_ARABIC
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences.KEY_TEXT_SIZE_PER_TRANSLATION
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import com.alfaazplus.sunnah.ui.utils.shared_preference.PrefKey
import com.alfaazplus.sunnah.ui.utils.text.ArabicTextStyleParams
import com.alfaazplus.sunnah.ui.utils.text.TranslationTextStyleParams
import com.alfaazplus.sunnah.ui.utils.text.getArabicTextStyle
import com.alfaazplus.sunnah.ui.utils.text.getTranslationTextStyle
import kotlinx.coroutines.launch

@Composable
fun HadithTextPreview(
    sizePercent: Int,
    isArabic: Boolean,
    previewText: String,
    isSerif: Boolean,
) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val style = if (isArabic) {
        getArabicTextStyle(
            params = ArabicTextStyleParams(
                colors = colors,
                type = typography,
                sizePercent = sizePercent,
            )
        )
    } else {
        getTranslationTextStyle(
            params = TranslationTextStyleParams(
                colors = colors,
                type = typography,
                sizePercent = sizePercent,
                isSerif = isSerif,
            )
        )
    }

    Text(
        previewText,
        style = style,
    )
}

@Composable
private fun TextSizeSlider(key: PrefKey<Int>, title: Int, previewText: String, isSerif: Boolean = false) {
    val coroutineScope = rememberCoroutineScope()
    val textSizePercent = DataStoreManager.observe(key)

    val min = 50f
    val max = 200f
    val steps = max.toInt() - min.toInt()


    ListItemCategoryLabel(stringResource(title))
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Slider(
            modifier = Modifier.weight(1f),
            value = textSizePercent.toFloat(),
            onValueChange = {
                coroutineScope.launch {
                    DataStoreManager.write(key, it.toInt())
                }
            },
            valueRange = min..max, steps = steps,
        )
        Text(
            text = "${textSizePercent}%", modifier = Modifier.padding(start = 10.dp), style = MaterialTheme.typography.labelSmall
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides if (key == KEY_TEXT_SIZE_PER_ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            HadithTextPreview(textSizePercent, key == KEY_TEXT_SIZE_PER_ARABIC, previewText, isSerif)
        }
    }
}

@Composable
fun TextSizeSheet(isOpen: Boolean, onDismiss: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val isSerifFontStyle = ReaderPreferences.observeIsSerifFontStyle()

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
                KEY_TEXT_SIZE_PER_ARABIC,
                R.string.arabic_text_size,
                previewText = " مَنْ صَامَ رَمَضَانَ إِيمَانًا وَاحْتِسَابًا غُفِرَ لَهُ مَا تَقَدَّمَ مِنْ ذَنْبِهِ"
            )
            TextSizeSlider(
                KEY_TEXT_SIZE_PER_TRANSLATION,
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
                    DataStoreManager.write(KEY_IS_SERIF_FONT_STYLE, it)
                }
            }
        }
    }
}
