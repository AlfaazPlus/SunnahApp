package com.alfaazplus.sunnah.ui.components.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.common.Loader
import com.alfaazplus.sunnah.ui.models.BooksSearchResult
import com.alfaazplus.sunnah.ui.screens.BookMetaInfoCard
import com.alfaazplus.sunnah.ui.theme.fontUthmani
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.viewModels.SearchViewModel

@Composable
private fun BookSearchItem(
    item: BooksSearchResult,
    onNavigate: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        border = CardDefaults.outlinedCardBorder(),
        onClick = onNavigate,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row {
                Card(
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Text(
                        "${item.collectionName} : ${item.book.serialNumber}",
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }

            Text(
                text = item.info.title,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 5.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
            )

            Text(
                text = item.book.title,
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
                BookMetaInfoCard("Range: ${item.book.hadithStart} - ${item.book.hadithEnd}")
                BookMetaInfoCard("Total Hadith: ${item.book.hadithCount}")
            }
        }
    }
}

@Composable
fun BookSearchResults(vm: SearchViewModel, listState: LazyListState) {
    val navController = LocalNavHostController.current
    val booksSearchResults = vm.booksSearchResults.collectAsLazyPagingItems()
    val quickSearchResults by vm.quickBookResults.collectAsState()
    val isLoading = booksSearchResults.loadState.refresh is LoadState.Loading


    if (isLoading) {
        Loader(fill = true)
        return
    }


    if (booksSearchResults.itemCount == 0 && quickSearchResults.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No results found",
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        return
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 100.dp),
    ) {
        items(
            count = quickSearchResults.size,
            key = { index ->
                val item = quickSearchResults[index]
                "${item.bookId}-${item.collectionId}-quick"
            },
        ) { index ->
            val item = quickSearchResults[index]
            QuickHadithSearchResult(
                title = "${item.serialNumber}. ${item.bookTitle}",
                description = {
                    Text(
                        text = item.collectionName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Normal,
                    )
                },
            ) {
                navController.navigate(Routes.READER.args(item.collectionId, item.bookId))
            }
        }

        item {
            SearchResultCount(
                text = if (booksSearchResults.itemCount == 1) {
                    stringResource(R.string.one_result_found)
                } else {
                    stringResource(R.string.n_results_found, booksSearchResults.itemCount)
                }
            )
        }

        items(
            booksSearchResults.itemCount,
            key = { index ->
                val item = booksSearchResults[index]
                if (item != null) {
                    return@items "${item.book.id}-${item.book.collectionId}"
                } else {
                    index
                }
            },
        ) { index ->
            val item = booksSearchResults[index]
            if (item != null) {
                BookSearchItem(item) {
                    navController.navigate(
                        Routes.READER.args(item.book.collectionId, item.book.id)
                    )
                }
            }
        }
    }
}