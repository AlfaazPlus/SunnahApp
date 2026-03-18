package com.alfaazplus.sunnah.ui.components.reader

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet
import com.alfaazplus.sunnah.ui.components.dialogs.SimpleTooltip
import com.alfaazplus.sunnah.ui.components.hadith.CollectionIcon
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.models.HadithWithTranslation
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel


@Composable
private fun SearchBox(
    query: String,
    onQueryChange: (String) -> Unit,
    @StringRes placeholder: Int,
) {
    val bgColor = MaterialTheme.colorScheme.background

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
            )
            .padding(start = 12.dp, end = 12.dp, top = 12.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = bgColor,
            focusedContainerColor = bgColor,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
        ),
        trailingIcon = {
            if (query.isNotBlank()) {
                SimpleTooltip(stringResource(R.string.clear_search)) {
                    IconButton(onClick = {
                        onQueryChange("")
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_x), contentDescription = stringResource(R.string.clear_search)
                        )
                    }
                }
            }
        },
        placeholder = { Text(stringResource(placeholder)) },
        value = query,
        onValueChange = onQueryChange,
        textStyle = MaterialTheme.typography.titleSmall,
        shape = MaterialTheme.shapes.medium,
        singleLine = true,
    )
}

@Composable
private fun HadithItem(
    hwt: HadithWithTranslation,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.padding(bottom = 4.dp),
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.primary.alpha(0.1f) else MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text(
                text = "Hadith: ${hwt.hadith.hadithNumber}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = hwt.translation?.refInBook ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.alpha(0.8f),
            )
        }
    }
}

@Composable
private fun HadithList(
    modifier: Modifier,
    hadiths: List<HadithWithTranslation>,
    currentHadithNumber: String,
    onJumpToHadith: (HadithWithTranslation) -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredHadiths = remember(searchQuery, hadiths) {
        if (searchQuery.isBlank()) hadiths
        else {
            hadiths.filter {
                val searchString =
                    "${it.hadith.hadithNumber} ${it.translation?.refInBook ?: ""} ${it.translation?.refEn ?: ""} ${it.translation?.refUscMsa ?: ""}"

                searchString.contains(searchQuery, ignoreCase = true)
            }
        }
    }


    val hadithListState =
        rememberLazyListState(
            filteredHadiths
                .indexOfFirst { it.hadith.hadithNumber == currentHadithNumber }
                .takeIf { it != -1 } ?: 0)

    Column {
        SearchBox(
            query = searchQuery, onQueryChange = { searchQuery = it }, placeholder = R.string.search_hadith
        )
        LazyColumn(
            modifier = modifier,
            state = hadithListState,
            contentPadding = PaddingValues(12.dp),
        ) {
            items(
                filteredHadiths.size,
            ) {
                HadithItem(
                    hwt = filteredHadiths[it],
                    isActive = filteredHadiths[it].hadith.hadithNumber == currentHadithNumber,
                ) { onJumpToHadith(filteredHadiths[it]) }
            }
        }
    }
}

@Composable
private fun BookItem(
    bwi: BookWithInfo,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.padding(bottom = 4.dp),
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.primary.alpha(0.1f) else MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
        ) {
            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painterResource(R.drawable.vector_bg2), null, colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = bwi.book.serialNumber,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                if (bwi.info?.title != null) {
                    Text(
                        text = bwi.info.title,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = bwi.book.title,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = fontUthmani,
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
            Box(modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun BookList(
    books: List<BookWithInfo>,
    currentBookId: Int,
    onJumpToBook: (BookWithInfo) -> Unit,
    modifier: Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredBooks = remember(searchQuery, books) {
        if (searchQuery.isBlank()) books
        else {
            books.filter {
                val searchString =
                    "${it.book.serialNumber} ${it.book.title} ${it.book.intro} ${it.book.description} ${it.info?.title ?: ""} ${it.info?.intro ?: ""} ${it.info?.description ?: ""}"
                searchString.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val bookListState = rememberLazyListState(filteredBooks.indexOfFirst { it.book.id == currentBookId })

    Column {
        SearchBox(
            query = searchQuery, onQueryChange = { searchQuery = it }, placeholder = R.string.search_book
        )

        LazyColumn(
            modifier = modifier,
            state = bookListState,
            contentPadding = PaddingValues(12.dp),
        ) {
            items(filteredBooks.size, key = { index -> filteredBooks[index].book.id }) {
                BookItem(
                    bwi = filteredBooks[it],
                    isActive = filteredBooks[it].book.id == currentBookId,
                ) {
                    onJumpToBook(filteredBooks[it])
                }
            }
        }
    }
}

@Composable
fun ReaderHadithNavigator(
    modifier: Modifier = Modifier,
    isInBottomSheet: Boolean,
    books: List<BookWithInfo>,
    hadiths: List<HadithWithTranslation>,
    currentBookId: Int,
    currentHadithNumber: String,
    currentNavigatorTab: Int,
    onChangeNavigatorTab: (Int) -> Unit,
    onJumpToBook: (BookWithInfo) -> Unit,
    onJumpToHadith: (HadithWithTranslation) -> Unit,
) {
    val tabs = listOf("Books", "Hadiths")

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        TabRow(
            selectedTabIndex = currentNavigatorTab,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = currentNavigatorTab == index,
                    onClick = {
                        onChangeNavigatorTab(index)
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        when (currentNavigatorTab) {
            0 -> BookList(
                modifier = Modifier.fillMaxHeight(if (isInBottomSheet) 0.75f else 1f),
                books = books,
                currentBookId = currentBookId,
                onJumpToBook = onJumpToBook,
            )

            1 -> HadithList(
                modifier = Modifier.fillMaxHeight(if (isInBottomSheet) 0.75f else 1f),
                hadiths = hadiths,
                currentHadithNumber = currentHadithNumber,
                onJumpToHadith = onJumpToHadith,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderAppBar(
    readerVm: ReaderViewModel,
    isWideScreen: Boolean,
    currentHadithNumber: () -> String?,
    scrollBehavior: TopAppBarScrollBehavior,
    onJumpToBook: (BookWithInfo) -> Unit,
    onJumpToHadith: (HadithWithTranslation) -> Unit,
) {
    val collectionId = readerVm.collectionId
    val bookId = readerVm.bookId
    val books = readerVm.books.value!!
    val bwi = readerVm.bwi
    val hadiths = readerVm.hadithList

    var showBottomSheet by remember { mutableStateOf(false) }
    val navController = LocalNavHostController.current

    Surface(
        shadowElevation = 4.dp,
    ) {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                scrolledContainerColor = MaterialTheme.colorScheme.surface,
            ),
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .clip(MaterialTheme.shapes.large)
                        .clickable(
                            enabled = !isWideScreen,
                        ) {
                            showBottomSheet = true
                        },
                ) {
                    CollectionIcon(
                        collectionId = collectionId,
                        height = 40.dp,
                    )

                    if (!isWideScreen) {
                        Row {
                            Text(
                                modifier = Modifier.widthIn(max = 150.dp),
                                text = bwi!!.getOrThrow().info?.title ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 0.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_drop_down),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }

                BottomSheet(
                    isOpen = showBottomSheet, onDismiss = { showBottomSheet = false }, dragHandle = null
                ) {
                    ReaderHadithNavigator(
                        isInBottomSheet = true,
                        books = books,
                        hadiths = hadiths,
                        currentBookId = bookId.value ?: 0,
                        currentHadithNumber = currentHadithNumber() ?: "",
                        currentNavigatorTab = readerVm.currentNavigatorTab,
                        onChangeNavigatorTab = { readerVm.currentNavigatorTab = it },
                        onJumpToBook = { bwi ->
                            onJumpToBook(bwi)
                            showBottomSheet = false
                        },
                        onJumpToHadith = { hwt ->
                            onJumpToHadith(hwt)
                            showBottomSheet = false
                        },
                    )
                }
            },
            navigationIcon = {
                SimpleTooltip(text = stringResource(R.string.goBack)) {
                    IconButton(onClick = { navController.popBackStack() }) {
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
            scrollBehavior = scrollBehavior,
        )
    }
}