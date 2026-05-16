package com.alfaazplus.sunnah.ui.components.reader

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.relations.BookWithTranslation
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.dialogs.SimpleTooltip
import com.alfaazplus.sunnah.ui.components.hadith.CollectionIcon
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel


internal data class ReaderAppBarDimensions(
    val barHeight: Dp,
) {
    val expandedHeight: Dp
        get() = barHeight
}

@Composable
internal fun rememberAppBarDimensions(isWideScreen: Boolean): ReaderAppBarDimensions {
    return remember(isWideScreen) {
        if (isWideScreen) {
            ReaderAppBarDimensions(
                barHeight = 96.dp,
            )
        } else {
            ReaderAppBarDimensions(
                barHeight = 96.dp,
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
                            .clip(MaterialTheme.shapes.medium)
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

                            if (!isWideScreen) {
                                Row {
                                    Text(
                                        modifier = Modifier.widthIn(max = 150.dp),
                                        text = currentBook.getTitle() ?: "",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Normal,
                                        lineHeight = 0.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Icon(
                                        painter = painterResource(R.drawable.ic_arrow_drop_down),
                                        contentDescription = null,
                                        tint = colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
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
                },
                actions = {
                    LayoutChangerButton()

                    SimpleTooltip(text = stringResource(R.string.settings)) {
                        IconButton(onClick = { navController.navigate(route = Routes.SETTINGS.arg(true)) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_settings),
                                contentDescription = stringResource(R.string.settings),
                            )
                        }
                    }
                },
            )
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
