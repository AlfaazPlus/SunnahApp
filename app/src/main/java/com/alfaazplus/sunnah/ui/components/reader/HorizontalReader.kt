package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alfaazplus.sunnah.ui.models.ReaderLayoutItem
import com.alfaazplus.sunnah.ui.screens.LocalReaderScaffoldController
import com.alfaazplus.sunnah.ui.utils.reader.ReaderPreparedData
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel
import kotlinx.coroutines.flow.distinctUntilChanged


fun resolveReaderItemIndex(items: List<ReaderLayoutItem>, hadithId: String?): Int? {
    if (hadithId == null) {
        return 0
    }

    return items
        .indexOfFirst {
            it is ReaderLayoutItem.HadithUI && it.hadithId == hadithId
        }
        .takeIf { it >= 0 }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorizontalReader(
    readerVm: ReaderViewModel,
    preparedData: ReaderPreparedData,
    nestedScrollConnection: NestedScrollConnection,
) {
    val scaffoldController = LocalReaderScaffoldController.current
    val readerItems = preparedData.items

    val pagerState = rememberPagerState(
        initialPage = resolveReaderItemIndex(readerItems, readerVm.activeHadithId.value) ?: 0,
        pageCount = { readerItems.size },
    )

    DisposableEffect(pagerState) {
        scaffoldController.bottomBar = {
            HorizontalReaderBottomBar(readerVm, pagerState)
        }

        onDispose {
            scaffoldController.bottomBar = null
        }
    }


    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect {
                readerVm.updateLastKnownHadith(it)
            }
    }

    val navigateToHadith by readerVm.navigateToHadith.collectAsStateWithLifecycle()

    LaunchedEffect(navigateToHadith, readerItems) {
        val hadithId = navigateToHadith ?: return@LaunchedEffect

        val idx = readerItems.indexOfFirst { item ->
            item is ReaderLayoutItem.HadithUI && item.hadithId == hadithId
        }

        if (idx >= 0) {
            pagerState.scrollToPage(idx)
            readerVm.consumeHadithNavigation()
        }
    }

    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 1,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) { page ->
        val item = readerItems[page]

        key(item.key) {
            when (item) {
                is ReaderLayoutItem.HadithUI -> {
                    HadithItemView(
                        modifier = Modifier.nestedScroll(nestedScrollConnection),
                        hadithUi = item,
                        isVertical = false,
                    )
                }
            }
        }
    }
}
