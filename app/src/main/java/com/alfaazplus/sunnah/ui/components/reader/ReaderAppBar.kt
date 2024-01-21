package com.alfaazplus.sunnah.ui.components.reader

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.hadith.CollectionIcon
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.models.HadithWithTranslation
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.viewModels.BookListViewModel
import com.alfaazplus.sunnah.ui.viewModels.ReaderViewModel

@Composable
fun ItemDivider() {
    Divider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    )
}

@Composable
fun HadithItem(
    hadithNumber: String,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = "Hadith: $hadithNumber",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isActive) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 15.dp, vertical = 10.dp),
        color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
fun HadithList(
    hadiths: List<HadithWithTranslation>,
    currentHadithNumber: String,
    onJumpToHadith: (HadithWithTranslation) -> Unit
) {
    val hadithListState = rememberLazyListState(hadiths.indexOfFirst { it.hadith.hadithNumber == currentHadithNumber })

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .width(120.dp)
    ) {
        Text(
            "Hadiths",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.alpha(0.2f))
        ) {
            LazyColumn(state = hadithListState) {
                items(
                    hadiths.size,
                ) {
                    HadithItem(
                        hadithNumber = hadiths[it].hadith.hadithNumber,
                        isActive = hadiths[it].hadith.hadithNumber == currentHadithNumber,
                    ) { onJumpToHadith(hadiths[it]) }

                    if (it != hadiths.size - 1) {
                        ItemDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun BookItem(
    bwi: BookWithInfo,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isActive) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
    ) {
        Box(
            modifier = Modifier.size(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painterResource(R.drawable.vector_bg2),
                null,
                colorFilter = ColorFilter.tint(if (!isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
            )
            Text(
                text = bwi.book.serialNumber,
                color = if (!isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (bwi.info?.title != null) {
                Text(
                    text = bwi.info.title,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                )
            }
            Text(
                text = bwi.book.title,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                fontFamily = fontUthmani,
                color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
            )
        }
        Box(modifier = Modifier.size(28.dp))
    }
}

@Composable
fun BookList(
    books: List<BookWithInfo>,
    currentBookId: Int,
    onJumpToBook: (BookWithInfo) -> Unit,
    modifier: Modifier
) {
    val bookListState = rememberLazyListState(books.indexOfFirst { it.book.id == currentBookId })
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            "Books",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.alpha(0.2f))
        ) {
            LazyColumn(state = bookListState) {
                items(
                    books.size,
                    key = { index -> books[index].book.id }
                ) {
                    BookItem(
                        bwi = books[it],
                        isActive = books[it].book.id == currentBookId,
                    ) {
                        onJumpToBook(books[it])
                    }

                    if (it != books.size - 1) {
                        ItemDivider()
                    }
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
            books = books,
            currentBookId = currentBookId,
            onJumpToBook = onJumpToBook,
            modifier = Modifier.weight(1f)
        )
        Divider(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxHeight()
                .width(6.dp)
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
    currentHadithNumber: String,
    scrollBehavior: TopAppBarScrollBehavior,
    onJumpToBook: (BookWithInfo) -> Unit,
    onJumpToHadith: (HadithWithTranslation) -> Unit,
) {
    val collectionId = readerVm.collectionId
    val bookId = readerVm.bookId
    val books = readerVm.books
    val hadiths = readerVm.hadithList

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val navController = LocalNavHostController.current

    val currentBookNumber = books.find { it.book.id == bookId.value }?.book?.serialNumber ?: ""

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
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

                        Text(
                            text = "Book $currentBookNumber • Hadith $currentHadithNumber",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 0.sp,
                        )
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
                                currentHadithNumber = currentHadithNumber,
                                onJumpToBook = { bwi ->
                                    onJumpToBook(bwi)
                                    showBottomSheet = false
                                },
                                onJumpToHadith = { hwt ->
                                    onJumpToHadith(hwt)
                                    showBottomSheet = false
                                }
                            )
                        }
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowLeft,
                        contentDescription = "Go Back"
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { navController.navigate(route = Routes.SETTINGS) },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Settings"
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }
}