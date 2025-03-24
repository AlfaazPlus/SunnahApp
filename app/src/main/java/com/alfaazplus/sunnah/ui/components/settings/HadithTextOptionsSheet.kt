package com.alfaazplus.sunnah.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.RadioItem
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet
import com.alfaazplus.sunnah.ui.utils.ReaderUtils
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HadithTextOptionsSheet(isOpen: Boolean, onClose: () -> Unit) {
    val selectedHadithTextOption = DataStoreManager.observe(stringPreferencesKey(Keys.HADITH_TEXT_OPTION), ReaderUtils.HADITH_TEXT_OPTION_BOTH)
    val coroutineScope = rememberCoroutineScope()

    val items = listOf(
        Pair(ReaderUtils.HADITH_TEXT_OPTION_BOTH, R.string.show_arabic_and_translation),
        Pair(ReaderUtils.HADITH_TEXT_OPTION_ONLY_ARABIC, R.string.show_only_arabic),
        Pair(ReaderUtils.HADITH_TEXT_OPTION_ONLY_TRANSLATION, R.string.show_only_translation),
    )

    BottomSheet(
        isOpen = isOpen,
        onDismiss = onClose,
        icon = R.drawable.ic_hadith_text_option,
        title = stringResource(R.string.hadith_text_option),
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            items.forEach { (key, title) ->
                RadioItem(
                    title = title,
                    selected = key == selectedHadithTextOption,
                    onClick = {
                        coroutineScope.launch {
                            DataStoreManager.write(stringPreferencesKey(Keys.HADITH_TEXT_OPTION), key)

                            withContext(Dispatchers.Main) {
                                onClose()
                            }
                        }
                    }
                )
            }
        }
    }
}