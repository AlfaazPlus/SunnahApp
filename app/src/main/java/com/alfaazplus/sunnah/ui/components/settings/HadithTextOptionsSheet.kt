package com.alfaazplus.sunnah.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.RadioItem
import com.alfaazplus.sunnah.ui.utils.ReaderUtils
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithTextOptionsSheet(isOpen: Boolean, onClose: () -> Unit) {
    val selectedHadithTextOption = DataStoreManager.observe(stringPreferencesKey(Keys.HADITH_TEXT_OPTION), ReaderUtils.HADITH_TEXT_OPTION_BOTH)
    val hadithTextOptionsSheetState = rememberModalBottomSheetState(true)
    val coroutineScope = rememberCoroutineScope()

    val items = listOf(
        Pair(ReaderUtils.HADITH_TEXT_OPTION_BOTH, R.string.show_arabic_and_translation),
        Pair(ReaderUtils.HADITH_TEXT_OPTION_ONLY_ARABIC, R.string.show_only_arabic),
        Pair(ReaderUtils.HADITH_TEXT_OPTION_ONLY_TRANSLATION, R.string.show_only_translation),
    )

    if (!isOpen) {
        return
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = hadithTextOptionsSheetState,
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