package com.alfaazplus.sunnah.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.components.dialogs.SimpleTooltip
import com.alfaazplus.sunnah.ui.components.search.BookSearchResults
import com.alfaazplus.sunnah.ui.components.search.GlobalSearchTextField
import com.alfaazplus.sunnah.ui.components.search.HadithSearchResults
import com.alfaazplus.sunnah.ui.components.search.ScholarsSearchResults
import com.alfaazplus.sunnah.ui.components.search.SearchFilterSheet
import com.alfaazplus.sunnah.ui.components.search.SearchIndexingBanner
import com.alfaazplus.sunnah.ui.components.search.SearchResultTab
import com.alfaazplus.sunnah.ui.components.search.SearchResultTabs
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.viewModels.SearchViewModel

@Composable
fun SearchScreen(
    withBackButton: Boolean = true,
    vm: SearchViewModel = hiltViewModel(),
) {
    vm.primaryColor = colorScheme.primary

    var showSearchFilterSheet by remember { mutableStateOf(false) }

    val hadithListState = rememberLazyListState()
    val bookListState = rememberLazyListState()
    val scholarsListState = rememberLazyListState()

    val currentFilters by vm.currentFilters.collectAsState()
    val searchQuery by vm.searchQuery.collectAsState()
    val isSearchIndexing by vm.isSearchIndexing.collectAsState()
    var previousSearchQuery by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(searchQuery) {
        if (previousSearchQuery != null && previousSearchQuery != searchQuery) {
            hadithListState.scrollToItem(0)
            bookListState.scrollToItem(0)
            scholarsListState.scrollToItem(0)
        }

        previousSearchQuery = searchQuery
    }

    Scaffold(
        contentWindowInsets = if (withBackButton) {
            ScaffoldDefaults.contentWindowInsets
        } else {
            ScaffoldDefaults.contentWindowInsets.only(
                WindowInsetsSides.Top + WindowInsetsSides.Horizontal,
            )
        },
        topBar = {
            AppBar(
                title = stringResource(R.string.global_search), showNavigationIcon = withBackButton,
                actions = {

                    SimpleTooltip(stringResource(R.string.search_filter)) {
                        IconButton(
                            onClick = { showSearchFilterSheet = true },
                        ) {
                            Box {
                                Icon(
                                    painterResource(R.drawable.ic_filter),
                                    contentDescription = stringResource(R.string.search_filter),
                                )
                                if (!currentFilters.isEmpty) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(8.dp)
                                            .background(colorScheme.primary, CircleShape)
                                    )
                                }
                            }
                        }
                    }
                },
                shadowElevation = 0.dp,
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            GlobalSearchTextField(vm)

            if (isSearchIndexing) {
                SearchIndexingBanner()
            }

            if (searchQuery.isBlank() && !isSearchIndexing) {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(colorScheme.surfaceContainer)
                )
            }

            if (searchQuery.isBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(28.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.CenterHorizontally),
                        tint = colorScheme.onBackground.alpha(0.6f),
                    )
                    Text(
                        text = stringResource(R.string.searchTips),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = colorScheme.onBackground.alpha(0.6f),
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

    SearchFilterSheet(
        isOpen = showSearchFilterSheet,
        searchVm = vm,
        currentFilters = currentFilters,
        onClose = { showSearchFilterSheet = false },
    )
}
