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
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.delay
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
        contentPadding = PaddingValues(bottom = 120.dp),
        state = listState,
    ) {
        items(totalHadiths, key = { hadithList[it].hadith.urn }) { index ->
            val hadithItem = hadithList[index]

            HadithItem(
                vm.cwi!!.getOrThrow(),
                vm.bwi!!.getOrThrow(),
                hadithItem,
                true,
                vm.highlightedHadithNumber == hadithItem.hadith.hadithNumber,
            )

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
    val hadithList = vm.parsedHadithList
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val listState = rememberLazyListState()


    val navigateToHadith = remember {
        { hadithNumber: String ->
            val index = hadithList.indexOfFirst { parsedHadith ->
                parsedHadith.hadith.hadithNumber == hadithNumber
            }
            if (index >= 0) {
                coroutineScope.launch {
                    listState.scrollToItem(index)
                }
            }
        }
    }

    val navigateToBook = remember {
        { bookId: Int ->
            vm.bookId.value = bookId

            coroutineScope.launch {
                listState.scrollToItem(0)
            }
        }
    }

    LaunchedEffect(Unit) {
        vm.currentHadithNumberRetriever = {
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            val fullVisibleItem = visibleItems.firstOrNull { it.offset >= -250 } ?: visibleItems.firstOrNull()

            fullVisibleItem?.let { hadithList.getOrNull(it.index)?.hadith?.hadithNumber }
        }
    }

    LaunchedEffect(Unit) {
        vm.highlightedHadithNumber = ""

        val initialHNo = vm.initialHadithNumber
        val transientScroll = vm.transientScroll

        if (initialHNo.first != null && !initialHNo.second) {
            navigateToHadith(initialHNo.first!!)
            vm.initialHadithNumber = Pair(initialHNo.first, true)
            vm.highlightedHadithNumber = initialHNo.first!!

            delay(2000)

            vm.highlightedHadithNumber = ""
        } else {
            transientScroll
                .get()
                ?.let {
                    navigateToHadith(it)
                }
        }

        coroutineScope.launch {
            DataStoreManager.observeWithCallback(stringPreferencesKey(Keys.HADITH_LAYOUT)) { layout ->
                if (vm.hadithLayout != layout) {
                    vm.transientScroll.set(vm.currentHadithNumberRetriever())
                    vm.hadithLayout = layout
                }
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
                currentHadithNumber = vm.currentHadithNumberRetriever,
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