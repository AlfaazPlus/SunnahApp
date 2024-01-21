package com.alfaazplus.sunnah.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.RadioItem
import com.alfaazplus.sunnah.ui.utils.ReaderUtils

@Composable
fun HadithTextOptionsSheet(selectedHadithTextOption: String, onChange: (String) -> Unit) {
    val items = listOf(
        Pair(ReaderUtils.HADITH_TEXT_OPTION_BOTH, R.string.show_arabic_and_translation),
        Pair(ReaderUtils.HADITH_TEXT_OPTION_ONLY_ARABIC, R.string.show_only_arabic),
        Pair(ReaderUtils.HADITH_TEXT_OPTION_ONLY_TRANSLATION, R.string.show_only_translation),
    )

    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        items.forEach { (key, title) ->
            RadioItem(
                title = title,
                selected = key == selectedHadithTextOption,
                onClick = {
                    onChange(key)
                }
            )
        }
    }
}