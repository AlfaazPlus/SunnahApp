package com.alfaazplus.sunnah.ui.components.common

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.dialogs.SimpleTooltip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
) {
    val navController = LocalNavHostController.current
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            SimpleTooltip(
                text = stringResource(R.string.goBack)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = stringResource(R.string.goBack)
                    )
                }
            }
        }
    )
}