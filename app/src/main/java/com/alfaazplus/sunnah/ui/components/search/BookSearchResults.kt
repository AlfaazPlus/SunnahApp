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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.models.BooksSearchResult
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
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row {
                Card(
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        item.collectionName,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }

            Text(
                text = item.info.title,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun BookSearchResults(vm: SearchViewModel, listState: LazyListState) {
    val navController = LocalNavHostController.current
    val booksSearchResults = vm.booksSearchResults.collectAsLazyPagingItems()

    if (booksSearchResults.itemCount == 0) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center,
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
                "${item?.book?.id}-${item?.book?.collectionId}"
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