package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.dialogs.SimpleTooltip
import com.alfaazplus.sunnah.ui.utils.preferences.HadithLayout
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import kotlinx.coroutines.launch

@Composable
fun LayoutChangerButton() {
    val coroutineScope = rememberCoroutineScope()
    val selectedLayoutOption = ReaderPreferences.observeHadithLayout()

    fun onSelected(option: HadithLayout) {
        if (option == selectedLayoutOption) return

        coroutineScope.launch {
            ReaderPreferences.setHadithLayout(option)
        }
    }

    if (selectedLayoutOption == HadithLayout.VERTICAL) {
        SimpleTooltip(text = stringResource(R.string.change_to_horizontal_layout)) {
            IconButton(onClick = { onSelected(HadithLayout.HORIZONTAL) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_carousel_horizontal),
                    contentDescription = stringResource(R.string.change_to_horizontal_layout),
                )
            }
        }
    } else {
        SimpleTooltip(text = stringResource(R.string.change_to_vertical_layout)) {
            IconButton(onClick = { onSelected(HadithLayout.VERTICAL) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_carousel_vertical),
                    contentDescription = stringResource(R.string.change_to_vertical_layout),
                )
            }
        }
    }
}
