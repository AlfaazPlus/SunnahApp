package com.alfaazplus.sunnah.ui.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.models.scholars.Scholar
import com.alfaazplus.sunnah.helpers.ScholarsHelper
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.common.Loader
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.viewModels.SearchViewModel

@Composable
private fun ScholarSearchItem(
    item: Scholar,
    onNavigate: () -> Unit,
) {
    val birthText = item.birthDate?.takeIf { it.isNotBlank() } ?: "—"
    val deathText = item.deathDate?.takeIf { it.isNotBlank() } ?: "—"

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
            Text(
                text = item.shortName ?: "",
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(ScholarsHelper.getScholarRankColor(item.rank))
                )
                Text(
                    text = ScholarsHelper.getScholarRankName(item.rank) ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Text(
                modifier = Modifier.padding(top = 10.dp),
                text = "Birth: $birthText\nDeath: $deathText",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
fun ScholarsSearchResults(vm: SearchViewModel, listState: LazyListState) {
    val navController = LocalNavHostController.current
    val scholarsSearchResults = vm.scholarsSearchResults.collectAsLazyPagingItems()
    val isLoading = scholarsSearchResults.loadState.refresh is LoadState.Loading


    if (isLoading) {
        Loader(fill = true)
        return
    }


    if (scholarsSearchResults.itemCount == 0) {
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
                text = if (scholarsSearchResults.itemCount == 1) {
                    stringResource(R.string.one_result_found)
                } else {
                    stringResource(R.string.n_results_found, scholarsSearchResults.itemCount)
                }
            )
        }

        items(
            scholarsSearchResults.itemCount,
            key = { index ->
                scholarsSearchResults[index]?.id ?: index
            },
        ) { index ->
            val item = scholarsSearchResults[index]
            if (item != null) {
                ScholarSearchItem(item) {
                    navController.navigate(
                        Routes.SCHOLAR_INFO.arg(item.id)
                    )
                }
            }
        }
    }
}