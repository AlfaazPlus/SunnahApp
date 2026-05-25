package com.alfaazplus.sunnah.ui.components.common

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.dialogs.SimpleTooltip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String? = null,
    titleContent: (@Composable () -> Unit)? = null,
    containerColor: Color = colorScheme.surfaceContainer,
    contentColor: Color = colorScheme.onSurface,
    searchQuery: String = "",
    onSearchQueryChange: ((String) -> Unit)? = null,
    searchPlaceholder: String? = null,
    shadowElevation: Dp = 4.dp,
    showNavigationIcon: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var searchExpanded by rememberSaveable { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }
    val onSearch = onSearchQueryChange

    LaunchedEffect(searchExpanded) {
        if (searchExpanded) {
            searchFocusRequester.requestFocus()
        }
    }

    val searchEnabled = onSearch != null
    if (searchEnabled) {
        BackHandler(enabled = searchExpanded) {
            searchExpanded = false
        }
    }

    TopAppBar(
        modifier = Modifier.shadow(shadowElevation),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = containerColor,
            navigationIconContentColor = contentColor,
            titleContentColor = contentColor,
            actionIconContentColor = contentColor,
        ),
        title = {
            if (searchEnabled && searchExpanded) {
                SearchTextField(
                    value = searchQuery,
                    onValueChange = onSearch,
                    placeholder = searchPlaceholder ?: stringResource(R.string.search_hint),
                    modifier = Modifier.focusRequester(searchFocusRequester),
                )
            } else if (titleContent != null) {
                titleContent()
            } else if (title != null) {
                Text(text = title)
            }
        },
        navigationIcon = {
            if (showNavigationIcon) {
                BackButton {
                    if (searchEnabled && searchExpanded) {
                        searchExpanded = false
                    } else {
                        backPressedDispatcher?.onBackPressed()
                    }
                }
            }
        },
        actions = {
            if (!searchExpanded) {
                if (searchEnabled) {
                    val searchLabel = stringResource(R.string.search)
                    SimpleTooltip(text = searchLabel) {
                        IconButton(
                            onClick = { searchExpanded = true },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_search),
                                contentDescription = searchLabel,
                            )
                        }
                    }
                }
                actions()
            }
        },
    )
}

@Composable
fun BackButton(
    onClick: (() -> Unit)? = null,
) {
    val backLabel = stringResource(R.string.goBack)
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    fun handleClick() {
        if (onClick != null) onClick()
        else backPressedDispatcher?.onBackPressed()
    }


    SimpleTooltip(text = backLabel) {
        IconButton(
            onClick = ::handleClick,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = backLabel,
            )
        }
    }
}
