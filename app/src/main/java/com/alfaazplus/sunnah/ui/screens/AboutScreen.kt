package com.alfaazplus.sunnah.ui.screens


import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.BuildConfig
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.ListItem
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.helpers.NavigationHelper
import com.alfaazplus.sunnah.ui.utils.extension.copyToClipboard
import com.alfaazplus.sunnah.ui.utils.message.MessageUtils

private data class AboutItem(
    val icon: Int,
    val title: Int,
    val onClick: (context: Context) -> Unit,
)

private val items = listOf(
    AboutItem(
        R.drawable.ic_info, R.string.about_us,
        onClick = {
            NavigationHelper.openAboutUs(it)
        },
    ),
    AboutItem(
        R.drawable.ic_shield, R.string.privacy_policy,
        onClick = {
            NavigationHelper.openPrivacyPolicy(it)
        },
    ),
    AboutItem(
        R.drawable.ic_github_2, R.string.github,
        onClick = {
            NavigationHelper.openGithubRepo(it)
        },
    ),
)

@Composable
fun AboutScreen() {
    val context = LocalContext.current

    Scaffold(
        topBar = { AppBar(stringResource(R.string.about_us)) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            VersionCard()

            items.forEach {
                ListItem(
                    title = it.title,
                    leading = {
                        Icon(
                            painter = painterResource(it.icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = colorScheme.onSurface,
                        )
                    },
                    trailing = {
                        Icon(
                            painter = painterResource(R.drawable.ic_chevron_right),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    },
                ) { it.onClick(context) }
            }
        }
    }
}

@Composable
private fun VersionCard() {
    val context = LocalContext.current

    ListItem(
        title = R.string.app_version,
        subtitleStr = BuildConfig.VERSION_NAME,
        leading = {
            Icon(
                painter = painterResource(R.drawable.logo_icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified,
            )
        },
        trailing = {
            Icon(
                painter = painterResource(R.drawable.ic_clipboard),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        },
    ) {
        context.copyToClipboard(BuildConfig.VERSION_NAME)
        MessageUtils.showClipboardMessage(
            context,
            "Version name copied",
        )
    }
}
