package com.alfaazplus.sunnah.ui.components.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.relations.CollectionWithTranslation
import com.alfaazplus.sunnah.ui.components.common.CheckboxItem
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet
import com.alfaazplus.sunnah.ui.search.SearchFilters
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.viewModels.CollectionListViewModel
import com.alfaazplus.sunnah.ui.viewModels.SearchViewModel


@Composable
fun SearchFilterSheet(
    isOpen: Boolean,
    onClose: () -> Unit,
    currentFilters: SearchFilters,
    searchVm: SearchViewModel,
    vm: CollectionListViewModel = hiltViewModel(),
) {
    BottomSheet(
        title = stringResource(R.string.search_filter),
        icon = R.drawable.ic_filter,
        isOpen = isOpen,
        onDismiss = onClose,
    ) {
        val availableCollections by vm.collections.collectAsState()
        var draft by remember(currentFilters, isOpen) { mutableStateOf(currentFilters) }

        fun applyFilters() {
            // cleanup
            val finalSelection: Set<String>? = draft.selectedCollections?.let {
                when {
                    it.isEmpty() -> emptySet()
                    it.size == availableCollections.size -> null
                    else -> it.toSet()
                }
            }

            searchVm.setFilters(
                draft.copy(
                    selectedCollections = finalSelection
                )
            )
            
            onClose()
        }

        Column {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CollectionsSection(
                    draft = draft,
                    onChange = { draft = it },
                    available = availableCollections,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = { draft = SearchFilters() }) {
                    Text(stringResource(R.string.reset))
                }

                Button(
                    enabled = draft.isValid && draft != currentFilters,
                    onClick = { applyFilters() },
                ) {
                    Text(stringResource(R.string.apply_filter))
                }
            }
        }
    }
}

@Composable
private fun CollectionsSection(
    draft: SearchFilters,
    onChange: (SearchFilters) -> Unit,
    available: List<CollectionWithTranslation>,
) {
    val translationLangCode = ReaderPreferences.observeHadithTranslation()

    val effectiveSelected = remember(draft.selectedCollections, available) {
        draft.selectedCollections ?: available
            .map { it.collection.id }
            .toSet()
    }

    val allSelected = effectiveSelected.size == available.size

    Logger.d(draft.selectedCollections?.size)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                SectionTitle(stringResource(R.string.search_in))
            }

            if (available.isNotEmpty()) {
                TextButton(
                    onClick = {
                        onChange(
                            draft.copy(
                            selectedCollections = if (allSelected) emptySet()
                        else available
                            .map { it.collection.id }
                            .toSet()))
                    },
                ) {
                    Text(
                        text = stringResource(
                            if (allSelected) R.string.clear
                            else R.string.selectAll
                        ),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            for (option in available) {
                val collectionId = option.collection.id
                val checked = effectiveSelected.contains(collectionId)

                CheckboxItem(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    title = "${option.getTitle(translationLangCode) ?: ""} (${option.getTitle("ar") ?: ""})",
                    checked = checked,
                    onCheckedChange = {
                        val next = effectiveSelected
                            .toMutableSet()
                            .apply {
                                if (checked) remove(collectionId) else add(collectionId)
                            }

                        onChange(draft.copy(selectedCollections = next.toSet()))
                    },
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = colorScheme.primary,
        modifier = modifier.padding(horizontal = 16.dp),
    )
}
