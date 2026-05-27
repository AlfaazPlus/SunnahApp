package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alfaazplus.sunnah.ui.models.ReaderLayoutItem
import com.alfaazplus.sunnah.ui.utils.reader.ReaderPreparedData
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun VerticalReader(
    readerVm: ReaderViewModel,
    preparedData: ReaderPreparedData,
    nestedScrollConnection: NestedScrollConnection,
) {
    val coroutineScope = rememberCoroutineScope()

    val readerItems = preparedData.items

    val activeHadithId by readerVm.activeHadithId.collectAsStateWithLifecycle()
    val navigateToHadith by readerVm.navigateToHadith.collectAsStateWithLifecycle()

    val initialIndex = remember(readerItems, activeHadithId) {
        resolveReaderItemIndex(readerItems, activeHadithId) ?: 0
    }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex,
    )

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { readerVm.updateLastKnownHadith(it) }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .distinctUntilChanged()
            .collect { readerVm.loadMoreItemsIfNeeded(it) }
    }

    LaunchedEffect(readerItems) {
        if (readerItems.isNotEmpty()) {
            readerVm.updateLastKnownHadith(listState.firstVisibleItemIndex)
        }
    }

    LaunchedEffect(navigateToHadith, readerItems) {
        val hadithId = navigateToHadith ?: return@LaunchedEffect

        val idx = readerItems.indexOfFirst { item ->
            item is ReaderLayoutItem.HadithUI && item.hadithId == hadithId
        }

        if (idx >= 0) {
            listState.scrollToItem(idx)
            readerVm.consumeHadithNavigation()
        } else if (!preparedData.isComplete) {
            readerVm.loadPageContainingHadithIfNeeded(hadithId)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 120.dp),
        state = listState,
    ) {
        items(readerItems, key = { it.key }) { item ->
            when (item) {
                is ReaderLayoutItem.HadithUI -> {
                    HadithItemView(
                        modifier = Modifier.nestedScroll(nestedScrollConnection),
                        hadithUi = item,
                        isVertical = true,
                    )
                }
            }
        }

        if (preparedData.isComplete) {
            item {
                VerticalReaderFooter(
                    readerVm,
                    onTopClick = {
                        coroutineScope.launch {
                            listState.scrollToItem(0)
                        }
                    },
                )
            }
        }
    }
}
