package com.alfaazplus.sunnah.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.common.BorderedCard
import com.alfaazplus.sunnah.ui.components.hadith.CollectionIcon
import com.alfaazplus.sunnah.ui.components.reader.AboutCollectionSheet
import com.alfaazplus.sunnah.ui.models.BookWithInfo
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.theme.type
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.viewModels.BookListViewModel

@Composable
fun BookMetaInfoCard(
    text: String
) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(PaddingValues(horizontal = 5.dp, vertical = 2.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun BookItem(
    bookWithInfo: BookWithInfo,
    onClick: () -> Unit,
) {
    val book = bookWithInfo.book
    val info = bookWithInfo.info

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        BorderedCard(
            padding = PaddingValues(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 15.dp),
            onClick = onClick
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier.size(38.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painterResource(R.drawable.vector_bg2),
                            null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                        Text(
                            text = book.serialNumber,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    Text(
                        text = info?.title ?: "",
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp, end = 8.dp, top = 5.dp),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.size(38.dp))
                }
                Text(
                    text = book.title,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    fontFamily = fontUthmani,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                ) {
                    BookMetaInfoCard("Range: ${book.hadithStart} - ${book.hadithEnd}")
                    BookMetaInfoCard("Total Hadith: ${book.hadithCount}")
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(
    collectionId: Int,
    onBookItemClick: (Int) -> Unit,
    vm: BookListViewModel = hiltViewModel()
) {
    LaunchedEffect(collectionId) {
        vm.setCollectionId(collectionId)
    }

    val coroutineScope = rememberCoroutineScope()

    val books = vm.books
    val cwi = vm.collectionWithInfo

    val totalHadiths = books.sumOf { it.book.hadithCount }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(25.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CollectionIcon(collectionId, height = 70.dp)
                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = cwi?.info?.name ?: "",
                        style = type.titleSmall,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier.padding(top = 5.dp),
                        text = "Total Books: ${books.size} • Total Hadiths: $totalHadiths",
                        style = type.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    if (cwi?.info?.intro != null) {
                        AboutCollectionSheet(cwi)
                    }
                }
            }
        }
        items(
            books,
            key = { bookWithInfo -> bookWithInfo.book.id }
        ) { bookWithInfo ->
            BookItem(
                bookWithInfo,
            ) { onBookItemClick(bookWithInfo.book.id) }
        }
    }

}

@Composable
fun BooksIndexScreen(collectionId: Int) {
    val navController = LocalNavHostController.current

    Scaffold {
        Box(
            modifier = Modifier.padding(it)
        ) {
            ScreenContent(
                collectionId,
                onBookItemClick = { bookId ->
                    navController.navigate(Routes.READER.args(collectionId, bookId))
                }
            )
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .padding(10.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    .size(38.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = stringResource(R.string.goBack),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = {
                },
                modifier = Modifier
                    .padding(10.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    .size(38.dp)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = stringResource(R.string.search),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}