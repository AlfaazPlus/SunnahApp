package com.alfaazplus.sunnah.ui.components.search

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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

    SecondaryTabRow(
        selectedTabIndex = tabs.keys.indexOf(currentTab.value),
        containerColor = colorScheme.surfaceContainer,
    ) {
        tabs.forEach { (tab, title) ->
            Tab(
                text = { Text(stringResource(title)) },
                selected = currentTab.value == tab,
                onClick = {
                    currentTab.value = tab
                },
                selectedContentColor = colorScheme.primary,
                unselectedContentColor = colorScheme.onSurface,
            )
        }
    }

    renderTabContent(currentTab.value)
}
