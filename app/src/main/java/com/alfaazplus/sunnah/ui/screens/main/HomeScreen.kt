package com.alfaazplus.sunnah.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.HadithOfTheDay
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.components.dialogs.SimpleTooltip
import com.alfaazplus.sunnah.ui.components.hadith.HadithCollectionList
import com.alfaazplus.sunnah.ui.components.homepage.AppUpdateBanner
import com.alfaazplus.sunnah.ui.components.homepage.TranslationUpdateBanner
import com.alfaazplus.sunnah.ui.utils.keys.Routes

@Composable
fun HomeScreen() {
    val navController = LocalNavHostController.current

    Scaffold(
        topBar = {
            AppBar(
                showNavigationIcon = false,
                titleContent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.logo_icon),
                            contentDescription = stringResource(R.string.app_name),
                            tint = colorResource(R.color.primary),
                        )

                        Text(
                            text = stringResource(R.string.app_name),
                            style = typography.titleLarge,
                            color = colorScheme.onSurface,
                            fontWeight = FontWeight.Black,
                        )
                    }
                },
                actions = {
                    SimpleTooltip(text = stringResource(R.string.settings)) {
                        IconButton(
                            onClick = { navController.navigate(route = Routes.SETTINGS.arg(false)) },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_settings),
                                contentDescription = stringResource(R.string.settings),
                                tint = colorScheme.onSurface,
                            )
                        }
                    }
                },
            )
        },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            AppUpdateBanner()
            TranslationUpdateBanner()
            HadithOfTheDay()
            HadithCollectionList(
                onCollectionClick = { collectionId ->
                    navController.navigate(route = Routes.BOOKS_INDEX.arg(collectionId))
                },
            )
        }
    }
}
