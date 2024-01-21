package com.alfaazplus.sunnah.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.ui.components.NoQuranAppAlert
import com.alfaazplus.sunnah.ui.components.hadith.HadithText
import com.alfaazplus.sunnah.ui.components.reader.ReaderAppBar
import com.alfaazplus.sunnah.ui.components.reader.ReaderBottomBar
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.models.ParsedHadith
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.utils.ReaderUtils
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.rememberPreference
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel
import kotlinx.coroutines.launch

private fun getPreviousBookId(books: List<BookWithInfo>, currentBookId: Int?): Int? {
    val currentOrder = books.firstOrNull { it.book.id == currentBookId }?.book?.orderInCollection ?: return null
    val previousOrder = currentOrder.minus(1)
    val previousBook = books.firstOrNull { it.book.orderInCollection == previousOrder }

    return previousBook?.book?.id
}

private fun getNextBookId(books: List<BookWithInfo>, currentBookId: Int?): Int? {
    val currentOrder = books.firstOrNull { it.book.id == currentBookId }?.book?.orderInCollection ?: return null
    val nextOrder = currentOrder.plus(1)
    val nextBook = books.firstOrNull { it.book.orderInCollection == nextOrder }

    return nextBook?.book?.id
}

private fun getPreviousHadithNumber(currentPage: Int, hadithList: List<ParsedHadith>, books: List<BookWithInfo>, currentBookId: Int?): String? {
    if (currentPage > 0) {
        return "Hadith ${hadithList[currentPage - 1].hadith.hadithNumber}"
    }

    /*val order = books.firstOrNull { it.book.id == currentBookId }?.book?.orderInCollection
    return if (order != null && order > 1) "book" else null*/

    return null
}

private fun getNextHadithNumber(currentPage: Int, hadithList: List<ParsedHadith>, books: List<BookWithInfo>, currentBookId: Int?): String? {
    if (currentPage < hadithList.size - 1) {
        return "Hadith ${hadithList[currentPage + 1].hadith.hadithNumber}"
    }

    /*val order = books.firstOrNull { it.book.id == currentBookId }?.book?.orderInCollection
    return if (order != null && order < books.size) "book" else null*/
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithItem(parsedHadith: ParsedHadith) {
    val hadithTextOption by rememberPreference(Keys.HADITH_TEXT_OPTION, ReaderUtils.HADITH_TEXT_OPTION_BOTH)
    val showSanad by rememberPreference(Keys.SHOW_SANAD, true)
    val noQuranAppSheetState = rememberModalBottomSheetState()
    var showNoQuranAppBottomSheet by remember { mutableStateOf(false) }

    val translation = parsedHadith.translation

    val hadithText = if (parsedHadith.hadithText == null || hadithTextOption == ReaderUtils.HADITH_TEXT_OPTION_ONLY_TRANSLATION) null
    else buildAnnotatedString {
        if (!parsedHadith.narratorPrefixText.isNullOrEmpty() && showSanad) {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground.alpha(0.7f))) { append(parsedHadith.narratorPrefixText) }
        }
        append(parsedHadith.hadithText)
        if (!parsedHadith.narratorSuffixText.isNullOrEmpty() && showSanad) {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground.alpha(0.7f))) { append(parsedHadith.narratorSuffixText) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 120.dp)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            HadithText(
                text = hadithText,
                fontFamily = fontUthmani,
                fontSize = 20.sp,
                lineHeight = 36.sp,
                modifier = Modifier.padding(bottom = 20.dp),
                quranAppNotInstallCallback = {
                    showNoQuranAppBottomSheet = true
                }
            )
        }
        if (parsedHadith.translationText != null && hadithTextOption != ReaderUtils.HADITH_TEXT_OPTION_ONLY_ARABIC) {
            if (translation != null && !translation.narratorPrefix.isNullOrEmpty()) {
                Text(
                    translation.narratorPrefix,
                    modifier = Modifier
                        .alpha(0.7f)
                        .padding(bottom = 10.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Normal
                )
            }

            HadithText(
                text = parsedHadith.translationText,
                quranAppNotInstallCallback = {
                    showNoQuranAppBottomSheet = true
                }
            )
        }

        if (showNoQuranAppBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showNoQuranAppBottomSheet = false },
                sheetState = noQuranAppSheetState,
            ) {
                NoQuranAppAlert()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReaderPager(
    paddingValues: PaddingValues,
    hadithList: List<ParsedHadith>,
    pagerState: PagerState
) {
    HorizontalPager(
        state = pagerState,
        contentPadding = paddingValues,
        beyondBoundsPageCount = 1,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) { page ->
        HadithItem(hadithList[page])
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageContent(
    paddingValues: PaddingValues,
    hadithList: List<ParsedHadith>,
    pagerState: PagerState
) {
    ReaderPager(paddingValues, hadithList, pagerState)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReaderScreen(
    collectionId: Int = 1,
    bookId: Int = 1,
    hadithNumber: String? = null,
    vm: ReaderViewModel = hiltViewModel(),
) {
    vm.primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(collectionId, bookId) {
        if (!vm.initialized) {
            vm.collectionId = collectionId
            vm.bookId.value = bookId
            vm.loadEssentials()
        }
    }

    val hadithList = vm.parsedHadithList

    val pagerState = rememberPagerState(
        initialPage = resolveInitialPage(hadithList, hadithNumber),
        pageCount = { hadithList.size },
    )

    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    fun navigateToIndex(index: Int, animate: Boolean = true) {
        coroutineScope.launch {
            if (animate) pagerState.animateScrollToPage(index)
            else pagerState.scrollToPage(index)
        }
    }

    fun navigateToBook(bookId: Int) {
        vm.bookId.value = bookId
        navigateToIndex(0, false)
    }

    if (hadithList.isEmpty()) {
        return
    }

    val previousHadithNumber = getPreviousHadithNumber(pagerState.currentPage, hadithList, vm.books, vm.bookId.value)
    val nextHadithNumber = getNextHadithNumber(pagerState.currentPage, hadithList, vm.books, vm.bookId.value)
    val currentHadithNumber = hadithList[pagerState.currentPage].hadith.hadithNumber

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ReaderAppBar(
                readerVm = vm,
                currentHadithNumber = currentHadithNumber,
                scrollBehavior = scrollBehavior,
                onJumpToBook = { navigateToBook(it.book.id) },
                onJumpToHadith = {
                    val newHadithNumber = it.hadith.hadithNumber
                    val index = hadithList.indexOfFirst { parsedHadith ->
                        parsedHadith.hadith.hadithNumber == newHadithNumber
                    }
                    if (index >= 0) {
                        navigateToIndex(index)
                    }
                }
            )
        },
        bottomBar = {
            ReaderBottomBar(
                prevHadithNumber = previousHadithNumber,
                nextHadithNumber = nextHadithNumber,
                onPreviousClick = {
                    /*if (previousHadithNumber == "book") {
                        getPreviousBookId(vm.books, vm.bookId.value)?.let { navigateToBook(it) }
                    } else {
                    }*/
                    navigateToIndex(pagerState.currentPage - 1)
                },
                onNextClick = {
                    /*if (nextHadithNumber == "book") {
                        getNextBookId(vm.books, vm.bookId.value)?.let { navigateToBook(it) }
                    } else {
                    }*/
                    navigateToIndex(pagerState.currentPage + 1)
                },
            )
        },
    ) { PageContent(it, hadithList, pagerState) }
}

fun resolveInitialPage(hadithList: List<ParsedHadith>, hadithNumber: String?): Int {
    if (hadithNumber == null) {
        return 0
    }

    return with(hadithList.indexOfFirst {
        it.hadith.hadithNumber == hadithNumber
    }) {
        return@with if (this >= 0) this else 0
    }
}