package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.dialogs.SimpleTooltip
import com.alfaazplus.sunnah.ui.utils.ReaderUtils
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import kotlinx.coroutines.launch

@Composable
fun LayoutChangerButton() {
    val coroutineScope = rememberCoroutineScope()
    val selectedLayoutOption = ReaderUtils.getHadithLayoutOption()

    fun onSelected(option: String) {
        if (option == selectedLayoutOption) return

        coroutineScope.launch {
            DataStoreManager.write(stringPreferencesKey(Keys.HADITH_LAYOUT), option)
        }
    }

    if (selectedLayoutOption == ReaderUtils.HADITH_LAYOUT_VERTICAL) {
        SimpleTooltip(text = stringResource(R.string.change_to_horizontal_layout)) {
            IconButton(onClick = { onSelected(ReaderUtils.HADITH_LAYOUT_HORIZONTAL) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_carousel_horizontal),
                    contentDescription = stringResource(R.string.change_to_horizontal_layout),
                )
            }
        }
    } else {
        SimpleTooltip(text = stringResource(R.string.change_to_vertical_layout)) {
            IconButton(onClick = { onSelected(ReaderUtils.HADITH_LAYOUT_VERTICAL) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_carousel_vertical),
                    contentDescription = stringResource(R.string.change_to_vertical_layout),
                )
            }
        }
    }
}