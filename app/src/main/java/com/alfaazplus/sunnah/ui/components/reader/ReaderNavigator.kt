package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.SearchTextField
import com.alfaazplus.sunnah.ui.components.reader.dialogs.BookItemCard
import com.alfaazplus.sunnah.ui.utils.extension.verticalFadingEdge
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel
import kotlinx.coroutines.launch


private enum class NavTab {
    Books,
    Hadiths
}

@Composable
fun ReaderNavigator(
    readerVm: ReaderViewModel,
    isInModal: Boolean,
    onClose: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val tabs = NavTab.entries

    var selectedTabIndex by readerVm.selectedNavigationTabIndex

    if (selectedTabIndex >= tabs.size) {
        selectedTabIndex = 0
    }

    val selectedTab = tabs[selectedTabIndex]

    fun navigateBook(bookId: String) {
        scope.launch {
            readerVm.initReader(bookId)

            onClose()
        }
    }

    fun navigateHadith(bookId: String, hadithId: String) {
        scope.launch {
            if (readerVm.activeBookId.value == bookId) {
                readerVm.requestHadithNavigation(hadithId)
            } else {
                readerVm.initReader(bookId, hadithId)
            }

            onClose()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(if (isInModal) 0.9f else 1f)
            .then(if (!isInModal) Modifier.background(colorScheme.surfaceContainer) else Modifier)
    ) {
        SecondaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = colorScheme.surface,
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            when (tab) {
                                NavTab.Books -> stringResource(R.string.books)
                                NavTab.Hadiths -> stringResource(R.string.hadiths)
                            }, style = typography.labelLarge
                        )
                    },
                )
            }
        }

        when (selectedTab) {
            NavTab.Books -> BookList(
                readerVm = readerVm,
                onBookSelected = ::navigateBook,
            )

            NavTab.Hadiths -> HadithList(
                readerVm = readerVm,
                onHadithSelected = ::navigateHadith,
            )
        }
    }
}

@Composable
private fun BookList(
    readerVm: ReaderViewModel,
    onBookSelected: (String) -> Unit,
) {
    val books by readerVm.books.collectAsStateWithLifecycle()
    val currentBookId by readerVm.activeBookId.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }

    val filteredBooks = remember(searchQuery, books) {
        if (searchQuery.isBlank()) books
        else {
            books.filter { bwt ->
                val searchString = buildString {
                    bwt.book.number?.let { append(it) }
                    bwt.translations.forEach { translation ->
                        translation.title?.let { append(it) }
                        translation.intro?.let { append(it) }
                        translation.notes?.let { append(it) }
                        translation.preamble?.let { append(it) }
                    }
                }

                searchString.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val activeIndex = filteredBooks
        .indexOfFirst { it.book.id == currentBookId }
        .fastCoerceAtLeast(0)

    val gridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = activeIndex,
        initialFirstVisibleItemScrollOffset = -100,
    )

    LaunchedEffect(activeIndex) {
        gridState.scrollToItem(activeIndex, -100)
    }


    Column {
        SearchTextField(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = stringResource(R.string.search_book),
        )

        BoxWithConstraints(
            Modifier.verticalFadingEdge(gridState)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (maxWidth < 800.dp) 1 else 2),
                modifier = Modifier.fillMaxWidth(),
                state = gridState,
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp, top = 16.dp, bottom = 64.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(filteredBooks.size, key = { index -> filteredBooks[index].book.id }) {
                    BookItemCard(
                        bwt = filteredBooks[it],
                        isCurrent = filteredBooks[it].book.id == currentBookId,
                    ) {
                        onBookSelected(filteredBooks[it].book.id)
                    }
                }
            }
        }
    }
}

@Composable
private fun HadithList(
    readerVm: ReaderViewModel,
    onHadithSelected: (bookId: String, hadithId: String) -> Unit,
) {
    val navigationItems by readerVm.hadithNavigationItems.collectAsStateWithLifecycle()

    val currentHadithId by readerVm.activeHadithId.collectAsStateWithLifecycle()

    if (navigationItems.isEmpty()) {
        return Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(R.string.selectABook))
        }
    }

    var searchQuery by remember { mutableStateOf("") }

    val filteredItems = remember(searchQuery, navigationItems) {
        if (searchQuery.isBlank()) navigationItems
        else {
            navigationItems.filter { item ->
                val searchString = buildString {
                    append(item.visibleNumbering)
                    item.number?.let { append(it) }
                }

                searchString.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val activeIndex = filteredItems
        .indexOfFirst { it.hadithId == currentHadithId }
        .fastCoerceAtLeast(0)

    val hadithGridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = activeIndex,
        initialFirstVisibleItemScrollOffset = -100,
    )

    LaunchedEffect(activeIndex) {
        hadithGridState.scrollToItem(activeIndex, -100)
    }


    Column {
        SearchTextField(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = stringResource(R.string.search_hadith),
        )

        BoxWithConstraints(
            Modifier.verticalFadingEdge(hadithGridState)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(if (maxWidth < 800.dp) 2 else 3),
                state = hadithGridState,
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 64.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    count = filteredItems.size,
                    key = { index -> filteredItems[index].hadithId },
                ) { index ->
                    val item = filteredItems[index]
                    val isCurrent = item.hadithId == currentHadithId

                    Surface(
                        onClick = { onHadithSelected(item.bookId, item.hadithId) },
                        shape = RoundedCornerShape(8.dp),
                        color = colorScheme.surface,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isCurrent) colorScheme.primary else colorScheme.outlineVariant.copy(
                                alpha = 0.4f
                            ),
                        ),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                        ) {
                            Text(
                                text = item.visibleNumbering,
                                style = typography.labelMedium,
                                color = colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}
