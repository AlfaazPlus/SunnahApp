package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.NoQuranAppAlert
import com.alfaazplus.sunnah.ui.components.hadith.HadithText
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo
import com.alfaazplus.sunnah.ui.models.ParsedHadith
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.utils.ReaderUtils
import com.alfaazplus.sunnah.ui.utils.composable.getArabicTextSize
import com.alfaazplus.sunnah.ui.utils.composable.getTranslationTextSize
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel
import kotlinx.coroutines.launch


private fun getPreviousHadithNumber(currentPage: Int, hadithList: List<ParsedHadith>): String? {
    if (currentPage > 0) {
        return "Hadith ${hadithList[currentPage - 1].hadith.hadithNumber}"
    }

    return null
}

private fun getNextHadithNumber(currentPage: Int, hadithList: List<ParsedHadith>): String? {
    if (currentPage < hadithList.size - 1) {
        return "Hadith ${hadithList[currentPage + 1].hadith.hadithNumber}"
    }

    /*val order = books.firstOrNull { it.book.id == currentBookId }?.book?.orderInCollection
    return if (order != null && order < books.size) "book" else null*/
    return null
}

private fun resolvePage(hadithList: List<ParsedHadith>, hadithNumber: String?): Int? {
    if (hadithNumber == null) {
        return 0
    }

    return hadithList.indexOfFirst {
        it.hadith.hadithNumber == hadithNumber
    }.takeIf { it >= 0 }
}


@Composable
private fun HadithGrade(hwt: ParsedHadith) {
    if (hwt.gradeType == null) return

    val color = when (hwt.gradeType) { // green
        "sahih" -> Color(0xFF4CAF50) // yellow
        "hasan" -> Color(0xFF9D912B) // red
        "daif" -> Color(0xFFF44336)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Text(
        "Grade: ${hwt.translation?.grades}", color = color,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(top = 16.dp),
    )
}

@Composable
private fun HadithActionBar(
    cwi: CollectionWithInfo,
    bwi: BookWithInfo,
    hadith: ParsedHadith,
) {
    var showReferenceSheet by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically

    ) {
        IconButton(modifier = Modifier
            .padding(0.dp)
            .size(32.dp), colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface
        ), onClick = { }) {
            Icon(
                modifier = Modifier.padding(6.dp), painter = painterResource(R.drawable.ic_ellipsis_vertical), contentDescription = "Menu"
            )
        }

        Box(
            modifier = Modifier.weight(1f)
        ) { }

        Card(shape = MaterialTheme.shapes.small, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onSurface
        ), onClick = { showReferenceSheet = true }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
            ) {
                Text(
                    "${cwi.info?.name}: ${hadith.hadith.hadithNumber}",
                    style = MaterialTheme.typography.labelMedium,
                )
                Icon(
                    modifier = Modifier.size(14.dp), painter = painterResource(R.drawable.ic_chevron_down), contentDescription = "Menu"
                )
            }
        }
    }
    HadithReferenceSheet(cwi, hadith, showReferenceSheet) {
        showReferenceSheet = false
    }
}

@Composable
fun HadithItem(cwi: CollectionWithInfo, bwi: BookWithInfo, parsedHadith: ParsedHadith, vertical: Boolean) {
    val hadithTextOption = ReaderUtils.getHadithTextOption()

    val showSanad = ReaderUtils.getIsSanadEnabled()
    var showNoQuranAppBottomSheet by remember { mutableStateOf(false) }
    val arabicTextSize = getArabicTextSize()
    val translationTextSize = getTranslationTextSize()
    val isSerifFontStyle = ReaderUtils.getIsSerifFontStyle()

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

    var modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(16.dp)

    if (!vertical) {
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(top = 10.dp, bottom = 120.dp)
    }

    Column(
        modifier = modifier
    ) {
        HadithActionBar(
            cwi = cwi, bwi = bwi, hadith = parsedHadith
        )

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            HadithText(text = hadithText,
                       fontFamily = fontUthmani,
                       fontSize = arabicTextSize.first,
                       lineHeight = arabicTextSize.second,
                       modifier = Modifier.padding(bottom = 20.dp),
                       quranAppNotInstallCallback = {
                           showNoQuranAppBottomSheet = true
                       })
        }
        if (parsedHadith.translationText != null && hadithTextOption != ReaderUtils.HADITH_TEXT_OPTION_ONLY_ARABIC) {
            if (translation != null && !translation.narratorPrefix.isNullOrEmpty()) {
                Text(
                    translation.narratorPrefix,
                    modifier = Modifier
                        .alpha(0.7f)
                        .padding(bottom = 10.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = if (isSerifFontStyle) FontFamily.Serif else FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal
                )
            }

            HadithText(text = parsedHadith.translationText,
                       fontSize = translationTextSize.first,
                       fontFamily = if (isSerifFontStyle) FontFamily.Serif else FontFamily.SansSerif,
                       lineHeight = translationTextSize.second,
                       quranAppNotInstallCallback = {
                           showNoQuranAppBottomSheet = true
                       })
        }

        HadithGrade(parsedHadith)

        NoQuranAppAlert(showNoQuranAppBottomSheet) {
            showNoQuranAppBottomSheet = false
        }
    }
}

@Composable
private fun PageContent(
    cwi: CollectionWithInfo,
    bwi: BookWithInfo,
    paddingValues: PaddingValues,
    hadithList: List<ParsedHadith>,
    pagerState: PagerState,
) {
    HorizontalPager(
        state = pagerState, contentPadding = paddingValues, beyondViewportPageCount = 1, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) { page ->
        HadithItem(cwi, bwi, hadithList[page], false)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorizontalReader(
    vm: ReaderViewModel,
) {
    val hadithList = vm.parsedHadithList
    val pagerState = rememberPagerState(
        initialPage = resolvePage(hadithList, vm.initialHadithNumber.first) ?: 0,
        pageCount = { hadithList.size },
    )

    LaunchedEffect(Unit) {
        val initialHNo = vm.initialHadithNumber
        val transientScroll = vm.transientScroll

        if (initialHNo.first != null && !initialHNo.second) {
            val index = resolvePage(hadithList, initialHNo.first)
            if (index != null) pagerState.scrollToPage(index)

            vm.initialHadithNumber = Pair(initialHNo.first, true)
        } else {
            transientScroll.get()?.let {
                val index = resolvePage(hadithList, it)
                if (index != null) pagerState.scrollToPage(index)
            }
        }

        DataStoreManager.observeWithCallback(stringPreferencesKey(Keys.HADITH_LAYOUT)) { layout ->
            if (vm.hadithLayout != layout) {
                vm.transientScroll.set(vm.currentHadithNumber)
                vm.hadithLayout = layout
            }
        }
    }


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

    val previousHadithNumber = getPreviousHadithNumber(pagerState.currentPage, hadithList)
    val nextHadithNumber = getNextHadithNumber(pagerState.currentPage, hadithList)
    val currentHadithNumber = hadithList[pagerState.currentPage].hadith.hadithNumber

    LaunchedEffect(currentHadithNumber) {
        vm.currentHadithNumber = currentHadithNumber
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ReaderAppBar(readerVm = vm, currentHadithNumber = {
                Logger.d("REQUEST CURRRENT HADITH NUM")
                currentHadithNumber
            }, scrollBehavior = scrollBehavior, onJumpToBook = { navigateToBook(it.book.id) }, onJumpToHadith = {
                val index = resolvePage(hadithList, it.hadith.hadithNumber)
                if (index != null) {
                    navigateToIndex(index)
                }
            })
        },
        bottomBar = {
            HorizontalReaderBottomBar(
                prevHadithNumber = previousHadithNumber,
                nextHadithNumber = nextHadithNumber,
                onPreviousClick = {/*if (previousHadithNumber == "book") {
                        getPreviousBookId(vm.books, vm.bookId.value)?.let { navigateToBook(it) }
                    } else {
                    }*/
                    navigateToIndex(pagerState.currentPage - 1)
                },
                onNextClick = {/*if (nextHadithNumber == "book") {
                        getNextBookId(vm.books, vm.bookId.value)?.let { navigateToBook(it) }
                    } else {
                    }*/
                    navigateToIndex(pagerState.currentPage + 1)
                },
            )
        },
    ) { PageContent(vm.cwi!!, vm.bwi!!, it, hadithList, pagerState) }
}