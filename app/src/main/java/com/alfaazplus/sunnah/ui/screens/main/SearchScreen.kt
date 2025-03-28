package com.alfaazplus.sunnah.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.ui.components.search.BookSearchResults
import com.alfaazplus.sunnah.ui.components.search.HadithSearchResults
import com.alfaazplus.sunnah.ui.components.search.ScholarsSearchResults
import com.alfaazplus.sunnah.ui.components.search.SearchResultTabs
import com.alfaazplus.sunnah.ui.components.search.SearchTextField
import com.alfaazplus.sunnah.ui.models.SearchResultTab
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Search for hadiths, books, or scholars",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
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