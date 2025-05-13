package com.alfaazplus.sunnah.ui.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.dialogs.SimpleTooltip
import com.alfaazplus.sunnah.ui.utils.ThemeUtils
import com.alfaazplus.sunnah.ui.viewModels.SearchViewModel

@Composable
fun SearchTextField(vm: SearchViewModel) {
    var showSearchFilterSheet by remember { mutableStateOf(false) }
    val searchQuery = vm.searchQuery.collectAsState().value

    val bgColor = MaterialTheme.colorScheme.background

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
            )
            .padding(start = 16.dp, end = 16.dp, bottom = if (searchQuery.isNotBlank()) 0.dp else 16.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = bgColor,
            focusedContainerColor = bgColor,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
        ),
        trailingIcon = {
            SimpleTooltip(stringResource(R.string.search_fitler)) {
                IconButton(onClick = {
                    showSearchFilterSheet = true
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter), contentDescription = stringResource(R.string.search_fitler)
                    )
                }
            }
        },
        placeholder = { Text(stringResource(R.string.search_placeholder)) },
        value = searchQuery,
        onValueChange = vm::onSearchQueryChanged,
        textStyle = MaterialTheme.typography.titleSmall,
        shape = MaterialTheme.shapes.medium,
        singleLine = true,
    )

    SearchFilterSheet(
        isOpen = showSearchFilterSheet,
        onClose = { showSearchFilterSheet = false },
        searchVm = vm,
    )
}