package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.alfaazplus.sunnah.ui.components.dialogs.SimpleTooltip
import com.alfaazplus.sunnah.ui.components.hadith.CollectionIcon
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.models.HadithWithTranslation
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel

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
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = hwt.translation?.refInBook ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.alpha(0.8f),
            )
        }
    }
}

@Composable
private fun HadithList(
    hadiths: List<HadithWithTranslation>,
    currentHadithNumber: String,
    onJumpToHadith: (HadithWithTranslation) -> Unit,
) {
    val hadithListState =
        rememberLazyListState(
            hadiths
                .indexOfFirst { it.hadith.hadithNumber == currentHadithNumber }
                .takeIf { it != -1 } ?: 0)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .width(140.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_hash),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(20.dp)
                    .alpha(0.8f)
            )
            Text(
                "Hadiths",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
            )
        }

        LazyColumn(state = hadithListState) {
            items(
                hadiths.size,
            ) {
                HadithItem(
                    hwt = hadiths[it],
                    isActive = hadiths[it].hadith.hadithNumber == currentHadithNumber,
                ) { onJumpToHadith(hadiths[it]) }
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
    val bookListState = rememberLazyListState(books.indexOfFirst { it.book.id == currentBookId })
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 16.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_book),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(20.dp)
                    .alpha(0.8f)
            )
            Text(
                "Books",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
            )
        }
        LazyColumn(state = bookListState) {
            items(books.size, key = { index -> books[index].book.id }) {
                BookItem(
                    bwi = books[it],
                    isActive = books[it].book.id == currentBookId,
                ) {
                    onJumpToBook(books[it])
                }
            }
        }
    }
}

@Composable
fun ReaderHadithNavigator(
    books: List<BookWithInfo>,
    hadiths: List<HadithWithTranslation>,
    currentBookId: Int,
    currentHadithNumber: String,
    onJumpToBook: (BookWithInfo) -> Unit,
    onJumpToHadith: (HadithWithTranslation) -> Unit,
) {

    Row(modifier = Modifier.padding(5.dp)) {
        BookList(
            books = books, currentBookId = currentBookId, onJumpToBook = onJumpToBook, modifier = Modifier.weight(1f)
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .width(6.dp), color = Color.Transparent
        )
        HadithList(
            hadiths = hadiths,
            currentHadithNumber = currentHadithNumber,
            onJumpToHadith = onJumpToHadith,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderAppBar(
    readerVm: ReaderViewModel,
    currentHadithNumber: () -> String,
    scrollBehavior: TopAppBarScrollBehavior,
    onJumpToBook: (BookWithInfo) -> Unit,
    onJumpToHadith: (HadithWithTranslation) -> Unit,
) {
    val collectionId = readerVm.collectionId
    val bookId = readerVm.bookId
    val books = readerVm.books.value!!
    val bwi = readerVm.bwi
    val hadiths = readerVm.hadithList

    val sheetState = rememberModalBottomSheetState()
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
                Surface(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = 10.dp)
                            .clip(MaterialTheme.shapes.large)
                            .clickable {
                                showBottomSheet = true
                            },
                    ) {
                        CollectionIcon(
                            collectionId = collectionId,
                            height = 40.dp,
                        )

                        Row {
                            Text(
                                modifier = Modifier.widthIn(max = 150.dp),
                                text = bwi?.info?.title ?: "",
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

                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showBottomSheet = false },
                            sheetState = sheetState,
                        ) {
                            ReaderHadithNavigator(
                                books = books,
                                hadiths = hadiths,
                                currentBookId = bookId.value ?: 0,
                                currentHadithNumber = currentHadithNumber(),
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
                    }
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