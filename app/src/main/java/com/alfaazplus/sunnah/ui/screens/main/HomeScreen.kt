package com.alfaazplus.sunnah.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
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
import com.alfaazplus.sunnah.ui.components.IndexMenuButton
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.components.hadith.HadithCollectionList
import com.alfaazplus.sunnah.ui.components.homepage.AppUpdateBanner
import com.alfaazplus.sunnah.ui.components.homepage.TranslationUpdateBanner
import com.alfaazplus.sunnah.ui.safeNavigate
import com.alfaazplus.sunnah.ui.utils.keys.Routes

@Composable
fun HomeScreen() {
    val navController = LocalNavHostController.current

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(
            WindowInsetsSides.Top + WindowInsetsSides.Horizontal,
        ),
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
                    IndexMenuButton()
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
                    navController.safeNavigate(route = Routes.BOOKS_INDEX.arg(collectionId))
                },
            )
        }
    }
}
