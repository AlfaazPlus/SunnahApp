package com.alfaazplus.sunnah.ui.components.reader

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.relations.BookWithTranslation
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.dialogs.SimpleTooltip
import com.alfaazplus.sunnah.ui.components.hadith.CollectionIcon
import com.alfaazplus.sunnah.ui.components.reader.dialogs.BookInfoSheet
import com.alfaazplus.sunnah.ui.models.ReaderLayoutItem
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.theme.tightTextStyle
import com.alfaazplus.sunnah.ui.utils.StringUtils
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel


internal data class ReaderAppBarDimensions(
    val barHeight: Dp,
    val headerHeight: Dp,
    val dividerHeight: Dp,
    val dividerCount: Int,
) {
    val expandedHeight: Dp
        get() = barHeight + headerHeight + (dividerHeight * dividerCount)
}

@Composable
internal fun rememberAppBarDimensions(isWideScreen: Boolean): ReaderAppBarDimensions {
    return remember(isWideScreen) {
        if (isWideScreen) {
            ReaderAppBarDimensions(
                barHeight = 86.dp,
                headerHeight = 0.dp,
                dividerHeight = 1.dp,
                dividerCount = 1,
            )
        } else {
            ReaderAppBarDimensions(
                barHeight = 86.dp,
                headerHeight = 40.dp,
                dividerHeight = 1.dp,
                dividerCount = 2,
            )
        }
    }
}

@Composable
fun rememberCurrentBook(readerVm: ReaderViewModel): BookWithTranslation? {
    val currentBookId by readerVm.activeBookId.collectAsStateWithLifecycle()
    val books by readerVm.books.collectAsStateWithLifecycle()

    return remember(currentBookId, books) {
        if (currentBookId == null || books.isEmpty()) return@remember null

        books.find { it.book.id == currentBookId }
    }
}

@Composable
fun rememberCurrentHadith(readerVm: ReaderViewModel): ReaderLayoutItem.HadithUI? {
    val currentHadithId by readerVm.activeHadithId.collectAsStateWithLifecycle()
    val preparedData by readerVm.preparedData.collectAsStateWithLifecycle()
    val items = preparedData?.items

    return remember(currentHadithId, items) {
        if (currentHadithId == null || items.isNullOrEmpty()) return@remember null

        items.find { it is ReaderLayoutItem.HadithUI && it.hadithId == currentHadithId } as? ReaderLayoutItem.HadithUI
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderAppBar(
    readerVm: ReaderViewModel,
    isWideScreen: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val appBarDims = rememberAppBarDimensions(isWideScreen)
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    var showNavigatorSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(true)

    val density = LocalDensity.current
    val heightOffset = scrollBehavior.state.heightOffset
    val visibleHeight = with(density) {
        (appBarDims.expandedHeight.toPx() + heightOffset)
            .coerceAtLeast(0f)
            .toDp()
    }

    val navController = LocalNavHostController.current

    val currentBook = rememberCurrentBook(readerVm)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp)
            .height(visibleHeight), shadowElevation = if (isWideScreen) 4.dp else 0.dp, color = colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier.requiredHeight(appBarDims.expandedHeight)
        ) {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(appBarDims.barHeight),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surfaceContainer,
                    scrolledContainerColor = colorScheme.surfaceContainer,
                ),
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(shapes.medium)
                            .clickable(
                                enabled = !isWideScreen,
                            ) { showNavigatorSheet = true }
                            .padding(horizontal = 10.dp),
                    ) {
                        if (currentBook != null) {
                            CollectionIcon(
                                collectionId = currentBook.book.collectionId,
                                height = 40.dp,
                            )
                        }
                    }
                },
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SimpleTooltip(text = stringResource(R.string.goBack)) {
                            IconButton(
                                onClick = {
                                    backPressedDispatcher?.onBackPressed()
                                }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_chevron_left),
                                    contentDescription = stringResource(R.string.goBack),
                                )
                            }
                        }

                        SimpleTooltip(text = stringResource(R.string.selectTranslation)) {
                            IconButton(
                                onClick = {
                                    navController.navigate(Routes.SETTINGS_TRANSLATIONS)
                                },
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_translations),
                                    contentDescription = stringResource(R.string.selectTranslation),
                                )
                            }
                        }
                    }
                },
                actions = {
                    LayoutChangerButton()

                    SimpleTooltip(text = stringResource(R.string.settings)) {
                        IconButton(onClick = { navController.navigate(route = Routes.SETTINGS.arg(false)) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_settings),
                                contentDescription = stringResource(R.string.settings),
                            )
                        }
                    }
                },
            )

            HorizontalDivider(
                thickness = appBarDims.dividerHeight, color = colorScheme.outlineVariant.alpha(0.5f)
            )

            if (!isWideScreen) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(appBarDims.headerHeight), contentAlignment = Alignment.Center
                ) {
                    Header(readerVm) {
                        if (!isWideScreen) {
                            showNavigatorSheet = true
                        }
                    }
                }

                HorizontalDivider(
                    thickness = appBarDims.dividerHeight, color = colorScheme.outlineVariant.alpha(0.5f)
                )
            }
        }
    }


    if (showNavigatorSheet && !isWideScreen) {
        ModalBottomSheet(
            onDismissRequest = { showNavigatorSheet = false },
            sheetState = sheetState,
            scrimColor = colorScheme.scrim.alpha(0.5f),
            containerColor = colorScheme.background,
            contentColor = colorScheme.onSurface,
            dragHandle = null,
            sheetGesturesEnabled = false,
            contentWindowInsets = { WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom) },
        ) {
            ReaderNavigator(
                readerVm = readerVm,
                isInModal = true,
            ) { showNavigatorSheet = false }
        }
    }
}

@Composable
private fun Header(
    readerVm: ReaderViewModel,
    onNavigatorRequest: () -> Unit,
) {
    val currentBook = rememberCurrentBook(readerVm) ?: return
    val currentHadith = rememberCurrentHadith(readerVm)
    val translationLangCode = ReaderPreferences.observeHadithTranslation()

    val textStyle = typography.labelMedium
        .copy(
            color = colorScheme.onSurfaceVariant,
        )
        .merge(
            tightTextStyle.copy(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = translationLangCode == "bn",
                ),
            )
        )

    var showInfoSheet by rememberSaveable { mutableStateOf(false) }

    Row(
        Modifier.padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .clip(shapes.medium)
                .clickable { onNavigatorRequest() }
                .padding(horizontal = 6.dp, vertical = 4.dp),
        ) {
            Text(
                modifier = Modifier
                    .widthIn(max = 150.dp)
                    .basicMarquee(),
                text = currentBook.getTitle(translationLangCode) ?: "…",
                style = textStyle,
                maxLines = 1,
            )

            Text(
                text = " : ${StringUtils.formatNumbering(currentHadith?.hwc?.hadith?.number, translationLangCode)}",
                style = textStyle,
            )

            Icon(
                painter = painterResource(R.drawable.ic_chevron_down),
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .clip(shapes.small)
                .background(color = colorScheme.surfaceVariant)
                .clickable { showInfoSheet = true }
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .alpha(0.8f), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_info),
                contentDescription = null,
                tint = colorScheme.onSurface,
                modifier = Modifier
                    .padding(end = 5.dp)
                    .size(16.dp)
            )
            Text(
                text = stringResource(R.string.info),
                style = typography.labelMedium.merge(tightTextStyle),
            )
        }
    }

    BookInfoSheet(
        bwt = currentBook.takeIf { showInfoSheet },
    ) {
        showInfoSheet = false
    }
}
