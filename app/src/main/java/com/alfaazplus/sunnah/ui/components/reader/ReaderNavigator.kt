package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.SearchTextField
import com.alfaazplus.sunnah.ui.components.reader.dialogs.BookItemCard
import com.alfaazplus.sunnah.ui.models.ReaderLayoutItem
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
            readerVm.initReaderIfNeeded(bookId)

            onClose()
        }
    }

    fun navigateHadith(bookId: String, hadithId: String) {
        scope.launch {
            val isInCurrentView = readerVm.preparedData.value?.items?.any { item ->
                item is ReaderLayoutItem.HadithUI && item.bookId == bookId && item.hadithId == hadithId
            } ?: false

            if (isInCurrentView) {
                readerVm.requestHadithNavigation(hadithId)
            } else {
                readerVm.initReaderIfNeeded(bookId, hadithId)
            }

            onClose()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(if (isInModal) 0.9f else 1f)
            .background(colorScheme.surfaceContainer)
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

    val bookListState = rememberLazyListState(filteredBooks.indexOfFirst { it.book.id == currentBookId })

    LazyColumn(
        state = bookListState,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 64.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        stickyHeader {
            SearchTextField(
                modifier = Modifier.padding(top = 16.dp),
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = stringResource(R.string.search_book),
            )
        }

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

@Composable
private fun HadithList(
    readerVm: ReaderViewModel,
    onHadithSelected: (bookId: String, hadithId: String) -> Unit,
) {
    val preparedData by readerVm.preparedData.collectAsStateWithLifecycle()
    val items = preparedData?.items ?: emptyList()

    val currentHadithId by readerVm.activeHadithId.collectAsStateWithLifecycle()

    if (items.isEmpty()) {
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

    val hadiths = remember(items) {
        items.filterIsInstance<ReaderLayoutItem.HadithUI>()
    }

    val filteredHadiths = remember(searchQuery, hadiths) {
        if (searchQuery.isBlank()) hadiths
        else {
            hadiths.filter { hadithUi ->
                val searchString = buildString {
                    append(hadithUi.visibleNumbering)
                    hadithUi.hwc.hadith.number?.let { append(it) }
                }

                searchString.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val hadithListState =
        rememberLazyListState(
            filteredHadiths
                .indexOfFirst { it.hadithId == currentHadithId }
                .takeIf { it != -1 } ?: 0)

    LazyColumn(
        state = hadithListState,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 64.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        stickyHeader {
            SearchTextField(
                modifier = Modifier.padding(top = 16.dp),
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = stringResource(R.string.search_hadith),
            )
        }

        items(
            filteredHadiths.size,
        ) {
            val item = filteredHadiths[it]

            HadithItem(
                hadithUi = item,
                isCurrent = item.hadithId == currentHadithId,
            ) { onHadithSelected(item.bookId, item.hadithId) }
        }
    }
}


@Composable
private fun HadithItem(
    hadithUi: ReaderLayoutItem.HadithUI,
    isCurrent: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isCurrent) colorScheme.primary
            else colorScheme.outlineVariant.copy(alpha = 0.4f),
        ),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Text(
                text = hadithUi.visibleNumbering,
                style = typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
