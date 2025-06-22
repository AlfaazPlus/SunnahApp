package com.alfaazplus.sunnah.ui.components.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.dialogs.SimpleTooltip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    bgColor: Color? = null,
    color: Color? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val navController = LocalNavHostController.current

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = bgColor ?: Color.Unspecified,
            navigationIconContentColor = color ?: Color.Unspecified,
            titleContentColor = color ?: Color.Unspecified,
            actionIconContentColor = color ?: Color.Unspecified,
        ),
        title = {
            Text(
                text = title,
            )
        },
        navigationIcon = {
            SimpleTooltip(
                text = stringResource(R.string.goBack)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        contentDescription = stringResource(R.string.goBack),
                    )
                }
            }
        },
        actions = actions,
    )
}