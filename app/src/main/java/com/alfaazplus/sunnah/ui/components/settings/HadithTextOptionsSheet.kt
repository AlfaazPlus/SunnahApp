package com.alfaazplus.sunnah.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.RadioItem
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet
import com.alfaazplus.sunnah.ui.utils.preferences.HadithTextOption
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import kotlinx.coroutines.launch

@Composable
fun HadithTextOptionsSheet(isOpen: Boolean, onClose: () -> Unit) {
    val selectedTranslationId = ReaderPreferences.observeHadithTranslation()
    val selectedHadithTextOption = ReaderPreferences.observeHadithTextOption()
    val arabicTextSizePercent = ReaderPreferences.observeTextSizePercentArabic()
    val translationTextSizePercent = ReaderPreferences.observeTextSizePercentTranslation()

    val coroutineScope = rememberCoroutineScope()

    val items = listOf(
        Pair(HadithTextOption.BOTH, R.string.show_arabic_and_translation),
        Pair(HadithTextOption.ONLY_ARABIC, R.string.show_only_arabic),
        Pair(HadithTextOption.ONLY_TRANSLATION, R.string.show_only_translation),
    )

    val showArabic = selectedHadithTextOption != HadithTextOption.ONLY_TRANSLATION
    val showTranslation = selectedHadithTextOption != HadithTextOption.ONLY_ARABIC

    BottomSheet(
        isOpen = isOpen,
        onDismiss = onClose,
        icon = R.drawable.ic_hadith_text_option,
        title = stringResource(R.string.hadith_text_option),
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (showTranslation) {
                        HadithTextPreview(
                            selectedTranslationId,
                            translationTextSizePercent,
                            false,
                            previewText = "The Prophet (ﷺ) said, \"The signs of a hypocrite are three: 1. Whenever he speaks, he tells a lie. 2. Whenever he promises, he always breaks it (his promise ). 3. If you trust him, he proves to be dishonest. (If you keep something as a trust with him, he will not return it.)\"",
                            false,
                        )
                    }

                    if (showArabic) {
                        HadithTextPreview(
                            selectedTranslationId,
                            arabicTextSizePercent,
                            true,
                            previewText = " آيَةُ الْمُنَافِقِ ثَلاَثٌ إِذَا حَدَّثَ كَذَبَ، وَإِذَا وَعَدَ أَخْلَفَ، وَإِذَا اؤْتُمِنَ خَانَ",
                            false,
                        )
                    }
                }
            }

            items.forEach { (option, title) ->
                RadioItem(
                    title = title, selected = option == selectedHadithTextOption,
                    onClick = {
                        coroutineScope.launch {
                            ReaderPreferences.setHadithTextOption(option)
                        }
                    },
                )
            }
        }
    }
}
