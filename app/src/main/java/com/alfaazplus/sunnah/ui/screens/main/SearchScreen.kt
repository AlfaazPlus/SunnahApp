package com.alfaazplus.sunnah.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.search.BookSearchResults
import com.alfaazplus.sunnah.ui.components.search.HadithSearchResults
import com.alfaazplus.sunnah.ui.components.search.ScholarsSearchResults
import com.alfaazplus.sunnah.ui.components.search.SearchResultTabs
import com.alfaazplus.sunnah.ui.components.search.SearchTextField
import com.alfaazplus.sunnah.ui.models.SearchResultTab
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.viewModels.SearchViewModel

@Composable
fun SearchScreen(
    vm: SearchViewModel = hiltViewModel(),
) {
    vm.primaryColor = MaterialTheme.colorScheme.primary

    val hadithListState = rememberLazyListState()
    val bookListState = rememberLazyListState()
    val scholarsListState = rememberLazyListState()
    val searchQuery = vm.searchQuery.collectAsState().value

    Column {
        SearchTextField(vm)

        if (searchQuery.isBlank()) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp, Alignment.CenterVertically)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.CenterHorizontally),
                    tint = MaterialTheme.colorScheme.onBackground.alpha(0.6f),
                )
                Text(
                    text = "Search for hadiths, books, or scholars",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.alpha(0.6f),
                )
            }
        } else {
            SearchResultTabs { tab ->
                when (tab) {
                    SearchResultTab.Hadiths -> HadithSearchResults(vm, hadithListState)
                    SearchResultTab.Books -> BookSearchResults(vm, bookListState)
                    SearchResultTab.Scholars -> ScholarsSearchResults(vm, scholarsListState)
                }
            }
        }
    }
}