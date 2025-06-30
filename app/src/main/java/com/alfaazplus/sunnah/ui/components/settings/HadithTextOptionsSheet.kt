package com.alfaazplus.sunnah.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.RadioItem
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet
import com.alfaazplus.sunnah.ui.utils.ReaderUtils
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import kotlinx.coroutines.launch

@Composable
fun HadithTextOptionsSheet(isOpen: Boolean, onClose: () -> Unit) {
    val selectedHadithTextOption = DataStoreManager.observe(stringPreferencesKey(Keys.HADITH_TEXT_OPTION), ReaderUtils.HADITH_TEXT_OPTION_BOTH)
    val coroutineScope = rememberCoroutineScope()

    val items = listOf(
        Pair(ReaderUtils.HADITH_TEXT_OPTION_BOTH, R.string.show_arabic_and_translation),
        Pair(ReaderUtils.HADITH_TEXT_OPTION_ONLY_ARABIC, R.string.show_only_arabic),
        Pair(ReaderUtils.HADITH_TEXT_OPTION_ONLY_TRANSLATION, R.string.show_only_translation),
    )

    val showArabic = selectedHadithTextOption != ReaderUtils.HADITH_TEXT_OPTION_ONLY_TRANSLATION
    val showTranslation = selectedHadithTextOption != ReaderUtils.HADITH_TEXT_OPTION_ONLY_ARABIC

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
                    if (showArabic) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            HadithTextPreview(
                                true,
                                previewText = " آيَةُ الْمُنَافِقِ ثَلاَثٌ إِذَا حَدَّثَ كَذَبَ، وَإِذَا وَعَدَ أَخْلَفَ، وَإِذَا اؤْتُمِنَ خَانَ",
                                false,
                            )
                        }
                    }

                    if (showTranslation) {
                        HadithTextPreview(
                            false,
                            previewText = "The Prophet (ﷺ) said, \"The signs of a hypocrite are three: 1. Whenever he speaks, he tells a lie. 2. Whenever he promises, he always breaks it (his promise ). 3. If you trust him, he proves to be dishonest. (If you keep something as a trust with him, he will not return it.)\"",
                            false,
                        )
                    }
                }
            }

            items.forEach { (key, title) ->
                RadioItem(
                    title = title, selected = key == selectedHadithTextOption,
                    onClick = {
                        coroutineScope.launch {
                            DataStoreManager.write(stringPreferencesKey(Keys.HADITH_TEXT_OPTION), key)
                        }
                    },
                )
            }
        }
    }
}