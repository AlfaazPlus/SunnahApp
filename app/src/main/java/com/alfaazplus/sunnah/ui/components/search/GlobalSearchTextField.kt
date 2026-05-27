package com.alfaazplus.sunnah.ui.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.viewModels.SearchViewModel

@Composable
fun GlobalSearchTextField(vm: SearchViewModel) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val searchQuery = vm.searchQuery.collectAsState().value

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colorScheme.surfaceContainer,
            )
            .padding(start = 16.dp, end = 16.dp, bottom = if (searchQuery.isNotBlank()) 0.dp else 16.dp)
            .focusRequester(focusRequester)
            .focusable()
            .onFocusChanged {
                if (it.isFocused) {
                    keyboardController?.show()
                }
            },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colorScheme.background,
            unfocusedContainerColor = colorScheme.background,
            focusedBorderColor = colorScheme.primary,
            unfocusedBorderColor = colorScheme.outline.alpha(0.3f),
        ),
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = {
                        vm.onSearchQueryChanged("")
                    },
                    modifier = Modifier.size(20.dp),
                ) {
                    Icon(
                        painterResource(R.drawable.ic_x),
                        contentDescription = stringResource(R.string.clear),
                        tint = colorScheme.onSurfaceVariant,
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
}
