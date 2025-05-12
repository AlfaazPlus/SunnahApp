package com.alfaazplus.sunnah.ui.components.appbars

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.dialogs.SimpleTooltip
import com.alfaazplus.sunnah.ui.utils.keys.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(
    title: Int?,
    showActions: Boolean = true,
) {
    val navController = LocalNavHostController.current

    TopAppBar(
        title = {
            Text(
                text = stringResource(title ?: R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Black,
            )
        },
        actions = {
            if (showActions) {
                SimpleTooltip(text = stringResource(R.string.manage_collections)) {
                    IconButton(
                        onClick = { navController.navigate(route = Routes.SETTINGS_MANAGE_COLLECTIONS) },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_book),
                            contentDescription = stringResource(R.string.manage_collections),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                SimpleTooltip(text = stringResource(R.string.settings)) {
                    IconButton(
                        onClick = { navController.navigate(route = Routes.SETTINGS.arg(false)) },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = stringResource(R.string.settings),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
    )
}