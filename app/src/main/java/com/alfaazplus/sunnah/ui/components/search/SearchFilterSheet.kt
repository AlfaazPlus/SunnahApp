package com.alfaazplus.sunnah.ui.components.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.CheckboxItem
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet
import com.alfaazplus.sunnah.ui.viewModels.CollectionListViewModel


@Composable
fun SearchFilterSheet(
    isOpen: Boolean,
    onClose: () -> Unit,
    vm: CollectionListViewModel = hiltViewModel(),
) {
    val selectedCollections = remember { mutableStateMapOf<Int, Boolean>() }
    val selectedSearchFor = remember { mutableStateMapOf<String, Boolean>() }
    val collections = vm.collections.collectAsState().value

    LaunchedEffect(Unit) {
        vm.loadCollections()
        vm.collections.value.forEach { c ->
            selectedCollections[c.collection.id] = true
        }
    }

    val searchForType = mapOf(
        "hadiths" to stringResource(R.string.hadiths),
        "books" to stringResource(R.string.books),
        "scholars" to stringResource(R.string.scholars),
    )

    BottomSheet(
        title = stringResource(R.string.search_fitler),
        icon = R.drawable.ic_filter,
        isOpen = isOpen,
        onDismiss = onClose,
    ) {
        Column {
            Column(
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    text = stringResource(R.string.search_in),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                collections.forEach { c ->
                    CheckboxItem(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        title = c.info?.name ?: "",
                        enabled = c.isDownloaded == true,
                        checked = if (c.isDownloaded == true) selectedCollections.getOrElse(c.collection.id) { false } else false,
                        onCheckedChange = {
                            selectedCollections[c.collection.id] = it
                        },
                    )
                }

                Text(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    text = stringResource(R.string.search_for),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                searchForType.forEach { (key, value) ->
                    CheckboxItem(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        title = value,
                        checked = selectedSearchFor.getOrElse(key) { true },
                        onCheckedChange = {
                            selectedSearchFor[key] = it
                        },
                    )
                }
            }

            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp), onClick = {}) {
                Text(text = "Apply")
            }
        }
    }
}