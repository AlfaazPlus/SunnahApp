package com.alfaazplus.sunnah.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.common.SearchTextField
import com.alfaazplus.sunnah.ui.components.hadith.CollectionIcon
import com.alfaazplus.sunnah.ui.components.reader.dialogs.AboutCollectionSheet
import com.alfaazplus.sunnah.ui.components.reader.dialogs.BookItemCard
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.theme.tightTextStyle
import com.alfaazplus.sunnah.ui.theme.type
import com.alfaazplus.sunnah.ui.utils.extension.bottomBorder
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.viewModels.BookListViewModel


@Composable
fun BooksIndexScreen(collectionId: String) {
    val navController = LocalNavHostController.current

    Box(
        modifier = Modifier.background(colorScheme.background)
    ) {
        ScreenContent(
            collectionId,
            onBookItemClick = { bookId ->
                navController.navigate(Routes.READER.args(bookId))
            },
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        ) {
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .background(colorScheme.surfaceContainer)
                    .statusBarsPadding()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .padding(10.dp)
                        .background(colorScheme.surfaceVariant, CircleShape)
                        .size(38.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        contentDescription = stringResource(R.string.goBack),
                        tint = colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(
    collectionId: String,
    onBookItemClick: (String) -> Unit,
    vm: BookListViewModel = hiltViewModel(),
) {
    LaunchedEffect(collectionId) {
        vm.setCollectionId(collectionId)
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }

    val books = vm.books
    val cwt = vm.cwt

    val totalBooks = books.size
    val totalHadiths = books.sumOf { it.hadithCount ?: 0 }

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

    BoxWithConstraints {
        val columnCount = maxOf(1, (maxWidth / 300.dp).toInt())

        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorScheme.surfaceContainer)
                        .statusBarsPadding()
                        .bottomBorder()
                        .padding(25.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CollectionIcon(collectionId, height = 70.dp)

                        Spacer(Modifier.height(12.dp))

                        cwt
                            ?.getTitle("en")
                            ?.let {
                                Text(
                                    text = it,
                                    style = typography.titleSmall,
                                    textAlign = TextAlign.Center,
                                )
                            }

                        cwt
                            ?.getTitle("ar")
                            ?.let {
                                Text(
                                    text = it,
                                    style = typography.titleMedium
                                        .merge(tightTextStyle)
                                        .copy(
                                            fontFamily = fontUthmani,
                                        ),
                                    textAlign = TextAlign.Center,
                                )
                            }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "${stringResource(R.string.totalBooks, totalBooks)} • ${stringResource(R.string.totalHadiths, totalHadiths)}",
                            style = type.bodyMedium,
                            color = colorScheme.onSurfaceVariant,
                        )

                        if (cwt != null) {
                            AboutCollectionSheet(cwt)
                        }
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SearchTextField(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    placeholder = stringResource(R.string.search_book),
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                )
            }

            items(
                count = filteredBooks.size,
                key = { index ->
                    val bwi = filteredBooks[index]
                    bwi.book.id
                },
            ) { index ->
                val bwt = filteredBooks[index]
                val leftPad = if (index % columnCount == 0) 16 else 0
                val rightPad = if (index % columnCount == columnCount - 1) 16 else 0

                BookItemCard(
                    modifier = Modifier
                        .padding(top = if (index == 0) 6.dp else 0.dp)
                        .padding(start = leftPad.dp, end = rightPad.dp),
                    bwt = bwt,
                ) { onBookItemClick(bwt.book.id) }
            }
        }
    }

}

@Composable
fun BookMetaInfoCard(
    text: String,
) {
    Box(
        modifier = Modifier
            .clip(shapes.extraSmall)
            .background(colorScheme.surfaceVariant)
            .padding(PaddingValues(horizontal = 5.dp, vertical = 2.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text, style = typography.bodyMedium
        )
    }
}
