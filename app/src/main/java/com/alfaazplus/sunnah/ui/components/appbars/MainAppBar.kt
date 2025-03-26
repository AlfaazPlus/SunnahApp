package com.alfaazplus.sunnah.ui.components.appbars

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.dialogs.SimpleTooltip
import com.alfaazplus.sunnah.ui.utils.keys.Routes

@Composable
fun MainAppBar(
    title: Int?,
) {
    val navController = LocalNavHostController.current

    Surface(
        color = MaterialTheme.colorScheme.surface, modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(title ?: R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Black,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 5.dp),
            )
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
    }
}