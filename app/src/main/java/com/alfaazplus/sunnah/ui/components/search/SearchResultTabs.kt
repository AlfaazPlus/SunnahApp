package com.alfaazplus.sunnah.ui.components.search

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.models.SearchResultTab
import com.alfaazplus.sunnah.ui.models.SearchResultTabSaver

@Composable
fun SearchResultTabs(
    renderTabContent: @Composable (tab: SearchResultTab) -> Unit,
) {
    val currentTab = rememberSaveable(stateSaver = SearchResultTabSaver) { mutableStateOf(SearchResultTab.Hadiths) }

    val tabs = mapOf(
        SearchResultTab.Hadiths to R.string.hadiths,
        SearchResultTab.Books to R.string.books,
        SearchResultTab.Scholars to R.string.scholars,
    )

    TabRow(
        selectedTabIndex = tabs.keys.indexOf(currentTab.value),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        tabs.forEach { (tab, title) ->
            Tab(
                text = { Text(stringResource(title)) },
                selected = currentTab.value == tab,
                onClick = {
                    currentTab.value = tab
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface,
            )
        }
    }

    renderTabContent(currentTab.value)
}