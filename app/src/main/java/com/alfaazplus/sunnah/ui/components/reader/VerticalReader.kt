package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfaazplus.sunnah.ui.models.ParsedHadith
import com.alfaazplus.sunnah.ui.utils.ThemeUtils
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel
import kotlinx.coroutines.launch


@Composable
private fun PageContent(
    vm: ReaderViewModel,
    hadithList: List<ParsedHadith>,
    listState: LazyListState,
) {
    val coroutineScope = rememberCoroutineScope()
    val books = vm.books.value!!

    fun scrollToTop(animate: Boolean = true) {
        coroutineScope.launch {
            if (animate) listState.animateScrollToItem(0)
            else listState.scrollToItem(0)
        }
    }

    fun navigateToBook(direction: Int) {
        val currentBookId = vm.bookId.value
        val currentBookIndex = books.indexOfFirst { it.book.id == currentBookId }

        if (currentBookIndex == -1) return

        val newBookIndex = currentBookIndex + direction
        if (newBookIndex < 0 || newBookIndex >= books.size) return

        val newBook = books.getOrNull(newBookIndex)?.book ?: return

        vm.bookId.value = newBook.id
        scrollToTop(false)
    }

    val totalHadiths = hadithList.size

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(top = 20.dp, bottom = 120.dp),
        state = listState,
    ) {
        items(totalHadiths) { index ->
            HadithItem(vm.cwi!!, vm.bwi!!, hadithList[index], true)

            HorizontalDivider(
                modifier = Modifier.padding(top = 20.dp)
            )
        }

        item {
            VerticalReaderFooter(vm, onPreviousClick = { navigateToBook(-1) }, onNextClick = { navigateToBook(1) }, onTopClick = { scrollToTop() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerticalReader(vm: ReaderViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val listState = rememberLazyListState()

    val hadithList = vm.parsedHadithList

    fun navigateToHadith(hadithNumber: String) {
        val index = hadithList.indexOfFirst { parsedHadith ->
            parsedHadith.hadith.hadithNumber == hadithNumber
        }
        if (index >= 0) {
            vm.currentHadithNumber = hadithNumber
            coroutineScope.launch {
                listState.scrollToItem(index)
            }
        }
    }

    fun navigateToBook(bookId: Int) {
        vm.bookId.value = bookId

        coroutineScope.launch {
            listState.scrollToItem(0)
        }
    }

    /*LaunchedEffect(listState) {
        snapshotFlow {
            listState.firstVisibleItemIndex
        }.collect { index ->
        }
    }*/

    LaunchedEffect(Unit) {
        val initialHNo = vm.initialHadithNumber
        val transientScroll = vm.transientScroll

        if (initialHNo.first != null && !initialHNo.second) {
            navigateToHadith(initialHNo.first!!)
            vm.initialHadithNumber = Pair(initialHNo.first, true)
        } else {
            transientScroll.get()?.let {
                navigateToHadith(it)
            }
        }

        DataStoreManager.observeWithCallback(stringPreferencesKey(Keys.HADITH_LAYOUT)) { layout ->
            if (vm.hadithLayout != layout) {
                val index = listState.firstVisibleItemIndex
                val hadithNumber = hadithList.getOrNull(index)?.hadith?.hadithNumber

                vm.transientScroll.set(hadithNumber)
                vm.hadithLayout = layout
            }
        }
    }

    val isDarkTheme = ThemeUtils.isDarkTheme()
    val bgColor = if (isDarkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surface
    val txtColor = if (isDarkTheme) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ReaderAppBar(
                readerVm = vm,
                currentHadithNumber = {
                    val fullVisibleItem = listState.layoutInfo.visibleItemsInfo.first {
                        it.offset >= -250
                    }
                    val index = fullVisibleItem.index
                    val hadithNumber = hadithList.getOrNull(index)?.hadith?.hadithNumber

                    hadithNumber ?: vm.currentHadithNumber
                },
                scrollBehavior = scrollBehavior,
                onJumpToBook = { navigateToBook(it.book.id) },
                onJumpToHadith = {
                    navigateToHadith(it.hadith.hadithNumber)
                },
            )
        },
        containerColor = bgColor,
        contentColor = txtColor,
    ) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            PageContent(vm, hadithList, listState)
        }
    }
}