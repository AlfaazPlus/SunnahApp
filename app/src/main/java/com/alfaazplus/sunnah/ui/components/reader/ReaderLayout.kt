package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alfaazplus.sunnah.ui.components.common.Loader
import com.alfaazplus.sunnah.ui.utils.preferences.HadithLayout
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel

@Composable
fun ReaderLayout(
    readerVm: ReaderViewModel,
    nestedScrollConnection: NestedScrollConnection,
) {
    val currentBookId by readerVm.activeBookId.collectAsStateWithLifecycle()

    val _layoutMode by readerVm.layoutMode.collectAsStateWithLifecycle()
    val layoutMode = _layoutMode ?: return Loader(fill = true)

    val prevLayoutMode = remember { mutableStateOf(layoutMode) }

    LaunchedEffect(layoutMode) {
        val prev = prevLayoutMode.value
        prevLayoutMode.value = layoutMode

        if (prev != layoutMode) {
            readerVm.handleHadithLayoutTransition(layoutMode)
        }
    }

    val _preparedData by readerVm.preparedData.collectAsStateWithLifecycle()
    val preparedData = _preparedData

    if (preparedData == null || currentBookId != preparedData.bookId) {
        return Loader(fill = true)
    }

    when (layoutMode) {
        HadithLayout.HORIZONTAL -> {
            HorizontalReader(readerVm, preparedData, nestedScrollConnection)
        }

        HadithLayout.VERTICAL -> {
            VerticalReader(readerVm, preparedData, nestedScrollConnection)
        }
    }
}
