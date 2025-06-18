package com.alfaazplus.sunnah.ui.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.common.Loader
import com.alfaazplus.sunnah.ui.models.HadithSearchResult
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.viewModels.SearchViewModel

@Composable
fun QuickHadithSearchResult(
    title: String,
    description: String?,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        border = CardDefaults.outlinedCardBorder(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.bolt),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
fun SearchResultCount(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outline)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
        )

        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outline)
        )
    }
}


@Composable
private fun HadithSearchItem(
    item: HadithSearchResult,
    onNavigate: () -> Unit,
) {
    var isMenuOpen by remember { mutableStateOf(false) }

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
                        "${item.collectionName}: ${item.hadith.hadithNumber}",
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Box(modifier = Modifier.weight(1f)) {}
                IconButton(
                    modifier = Modifier
                        .padding(0.dp)
                        .size(32.dp),
                    onClick = {
                        isMenuOpen = true
                    },
                ) {
                    Icon(
                        modifier = Modifier.padding(6.dp),
                        painter = painterResource(R.drawable.ic_ellipsis_vertical),
                        contentDescription = stringResource(R.string.desc_hadith_options),
                    )
                }
            }

            Text(
                text = item.translationText,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }

    HadithSearchItemMenu(
        item = item,
        isOpen = isMenuOpen,
        onClose = {
            isMenuOpen = false
        },
    )
}

@Composable
fun HadithSearchResults(vm: SearchViewModel, hadithListState: LazyListState) {
    val navController = LocalNavHostController.current
    val hadithSearchResults = vm.hadithsSearchResults.collectAsLazyPagingItems()
    val quickSearchResults by vm.quickHadithResults.collectAsState()
    val isLoading = hadithSearchResults.loadState.refresh is LoadState.Loading


    if (isLoading) {
        Loader(fill = true)
        return
    }


    if (hadithSearchResults.itemCount == 0 && quickSearchResults.isEmpty()) {
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
        state = hadithListState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 100.dp),
    ) {
        items(
            count = quickSearchResults.size,
            key = { index ->
                val item = quickSearchResults[index]
                "${item.hadithNumber}-${item.collectionId}-quick"
            },
        ) { index ->
            val item = quickSearchResults[index]

            QuickHadithSearchResult(
                title = "${item.collectionName}: ${item.hadithNumber}", description = "Book: ${item.bookTitle}"
            ) {
                navController.navigate(
                    Routes.READER.args(
                        item.collectionId,
                        item.bookId,
                        item.hadithNumber,
                    )
                )
            }
        }

        item {
            SearchResultCount(
                text = if (hadithSearchResults.itemCount == 1) {
                    stringResource(R.string.one_result_found)
                } else {
                    stringResource(R.string.n_results_found, hadithSearchResults.itemCount)
                }
            )
        }

        items(
            hadithSearchResults.itemCount,
            key = { index ->
                val item = hadithSearchResults[index]
                if (item != null) {
                    return@items item.hadith.urn
                } else {
                    index
                }
            },
        ) { index ->
            val item = hadithSearchResults[index]
            if (item != null) {
                HadithSearchItem(item) {
                    navController.navigate(
                        Routes.READER.args(
                            item.hadith.collectionId,
                            item.hadith.bookId,
                            item.hadith.hadithNumber,
                        )
                    )
                }
            }
        }
    }
}