package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.dialogs.SimpleTooltip
import com.alfaazplus.sunnah.ui.components.hadith.HadithText
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo
import com.alfaazplus.sunnah.ui.models.ParsedHadith
import com.alfaazplus.sunnah.ui.models.ReaderLayoutItem
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.ThemeUtils
import com.alfaazplus.sunnah.ui.utils.composable.getArabicTextSize
import com.alfaazplus.sunnah.ui.utils.composable.getTranslationTextSize
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel
import kotlinx.coroutines.launch


private fun getPreviousHadithNumber(currentPage: Int, hadithList: List<ParsedHadith>): String? {
    if (hadithList.isEmpty()) return null

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

    return hadithList
        .indexOfFirst {
            it.hadith.hadithNumber == hadithNumber
        }
        .takeIf { it >= 0 }
}

@Composable
private fun Modifier.highlightHadithItem(show: Boolean): Modifier {
    val alpha by animateFloatAsState(
        targetValue = if (show) 0.3f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "",
    )

    return this.background(MaterialTheme.colorScheme.primary.alpha(alpha))
}

@Composable
private fun HadithGrade(hadithUi: ReaderLayoutItem.HadithUI) {
    val gradeText = hadithUi.gradeText ?: return

    // todo: show description

    Card(
        modifier = Modifier.padding(top = 16.dp),
        shape = MaterialTheme.shapes.extraSmall,
        colors = CardDefaults.cardColors(
            containerColor = gradeText.colors.first, contentColor = gradeText.colors.second
        ),
    ) {
        Text(
            "Grade: ${gradeText.label}",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun HadithActionBar(
    hadithUi: ReaderLayoutItem.HadithUI,
) {
    val actions = LocalHadithActions.current
    val bgColor = MaterialTheme.colorScheme.surfaceContainer
    val txtColor = MaterialTheme.colorScheme.onSurface

    val navController = LocalNavHostController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SimpleTooltip(
            text = stringResource(R.string.desc_hadith_options)
        ) {
            IconButton(
                modifier = Modifier
                    .padding(0.dp)
                    .size(32.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = bgColor,
                    contentColor = txtColor,
                ),
                onClick = {
                    actions.onHadithOption(hadithUi.hadithId)
                },
            ) {
                Icon(
                    modifier = Modifier.padding(6.dp),
                    painter = painterResource(R.drawable.ic_ellipsis_vertical),
                    contentDescription = stringResource(R.string.desc_hadith_options),
                )
            }
        }

        if (!hadithUi.hasNarratorsChain) {
            SimpleTooltip(
                text = stringResource(R.string.desc_narrators_chain)
            ) {
                IconButton(
                    modifier = Modifier
                        .padding(0.dp)
                        .size(32.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = bgColor,
                        contentColor = txtColor,
                    ),
                    onClick = {
                        navController.navigate(route = Routes.NARRATOR_CHAIN.arg(hadithUi.hadithId))
                    },
                ) {
                    Icon(
                        modifier = Modifier.padding(6.dp),
                        painter = painterResource(R.drawable.ic_users),
                        contentDescription = stringResource(R.string.desc_narrators_chain),
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Card(
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(
                containerColor = bgColor,
                contentColor = txtColor,
            ),
            onClick = { actions.onNumberReferenceRequest(hadithUi.hadithId) },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
            ) {
                Text(
                    hadithUi.visibleNumbering,
                    style = MaterialTheme.typography.labelMedium,
                )
                Icon(
                    modifier = Modifier.size(14.dp), painter = painterResource(R.drawable.ic_chevron_down), contentDescription = "Menu"
                )
            }
        }
    }

}

@Composable
fun HadithItem(
    cwi: CollectionWithInfo,
    bwi: BookWithInfo,
    hadithUi: ReaderLayoutItem.HadithUI,
    vertical: Boolean,
    highlight: Boolean = false,
) {
    val hadithTextOption = ReaderPreferences.observeHadithTextOption()

    val showSanad = ReaderPreferences.observeIsSanadEnabled()
    val arabicTextSize = getArabicTextSize()
    val translationTextSize = getTranslationTextSize()
    val isSerifFontStyle = ReaderPreferences.observeIsSerifFontStyle()

    val arabicHadithText = if (parsedHadith.hadithText == null || hadithTextOption == ReaderPreferences.HADITH_TEXT_OPTION_ONLY_TRANSLATION) null
    else buildAnnotatedString {
        if (!parsedHadith.narratorPrefixText.isNullOrEmpty() && showSanad) {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground.alpha(0.7f))) { append(parsedHadith.narratorPrefixText) }
        }
        append(parsedHadith.hadithText)
        if (!parsedHadith.narratorSuffixText.isNullOrEmpty() && showSanad) {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground.alpha(0.7f))) { append(parsedHadith.narratorSuffixText) }
        }
    }

    val translationNarrator = parsedHadith.translationNarrator
    val translationHadithText = parsedHadith.translationText

    var modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .highlightHadithItem(highlight)

    if (!vertical) {
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 120.dp)
    }

    modifier = modifier.padding(16.dp)

    Column(
        modifier = modifier
    ) {
        HadithActionBar(hadithUi)

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            HadithText(
                text = arabicHadithText,
                fontFamily = fontUthmani,
                fontSize = arabicTextSize.first,
                lineHeight = arabicTextSize.second,
                modifier = Modifier.padding(bottom = 20.dp),
            )
        }

        if (translationHadithText != null && hadithTextOption != ReaderPreferences.HADITH_TEXT_OPTION_ONLY_ARABIC) {
            if (!translationNarrator.isNullOrEmpty()) {
                Text(
                    translationNarrator,
                    modifier = Modifier
                        .alpha(0.7f)
                        .padding(bottom = 10.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = if (isSerifFontStyle) FontFamily.Serif else FontFamily.SansSerif,
                    fontWeight = FontWeight.Normal,
                )
            }

            HadithText(
                text = translationHadithText,
                fontSize = translationTextSize.first,
                fontFamily = if (isSerifFontStyle) FontFamily.Serif else FontFamily.SansSerif,
                lineHeight = translationTextSize.second,
            )
        }

        HadithGrade(hadithUi)
    }
}

@Composable
private fun PageContent(
    cwi: CollectionWithInfo,
    bwi: BookWithInfo,
    hadithList: List<ParsedHadith>,
    pagerState: PagerState,
) {
    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 1,
        modifier = Modifier
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
    isWideScreen: Boolean,
) {
    val hadithList = vm.parsedHadithList
    val pagerState = rememberPagerState(
        initialPage = resolvePage(hadithList, vm.initialHadithId.first) ?: 0,
        pageCount = { hadithList.size },
    )

    LaunchedEffect(pagerState, hadithList) {
        vm.currentHadithNumberRetriever = {
            hadithList[pagerState.currentPage].hadith.hadithNumber
        }
    }


    LaunchedEffect(Unit) {
        val initialHNo = vm.initialHadithId
        val transientScroll = vm.transientScroll

        if (initialHNo.first != null && !initialHNo.second) {
            val index = resolvePage(hadithList, initialHNo.first)
            if (index != null) pagerState.scrollToPage(index)

            vm.initialHadithId = Pair(initialHNo.first, true)
        } else {
            transientScroll
                .get()
                ?.let {
                    val index = resolvePage(hadithList, it)
                    if (index != null) pagerState.scrollToPage(index)
                }
        }

        DataStoreManager.observeWithCallback(stringPreferencesKey(Keys.HADITH_LAYOUT)) { layout ->
            if (vm.hadithLayout != layout) {
                vm.transientScroll.set(vm.currentHadithNumberRetriever())
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

    fun navigateToHadith(hadithNumber: String) {
        val index = resolvePage(hadithList, hadithNumber)
        if (index != null) {
            navigateToIndex(index)
        }
    }

    val previousHadithNumber = getPreviousHadithNumber(pagerState.currentPage, hadithList)
    val nextHadithNumber = getNextHadithNumber(pagerState.currentPage, hadithList)

    val isDarkTheme = ThemeUtils.isDarkTheme()
    val bgColor = if (isDarkTheme) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surface
    val txtColor = if (isDarkTheme) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ReaderAppBar(
                readerVm = vm,
                isWideScreen = isWideScreen,
                currentHadithNumber = vm.currentHadithNumberRetriever,
                scrollBehavior = scrollBehavior,
                onJumpToBook = { navigateToBook(it.book.id) },
                onJumpToHadith = {
                    navigateToHadith(it.hadith.hadithNumber)
                },
            )
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
        containerColor = bgColor,
        contentColor = txtColor,
    ) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            if (isWideScreen) {
                ReaderLayout(
                    readerVm = vm,
                    currentHadithNumber = vm.currentHadithNumberRetriever,
                    onJumpToBook = { navigateToBook(it.book.id) },
                    onJumpToHadith = { hwt ->
                        navigateToHadith(hwt.hadith.hadithNumber)
                    },
                ) {
                    PageContent(
                        vm.cwi!!.getOrThrow(),
                        vm.bwi!!.getOrThrow(),
                        hadithList,
                        pagerState,
                    )
                }
            } else {
                PageContent(
                    vm.cwi!!.getOrThrow(),
                    vm.bwi!!.getOrThrow(),
                    hadithList,
                    pagerState,
                )
            }
        }

    }
}
