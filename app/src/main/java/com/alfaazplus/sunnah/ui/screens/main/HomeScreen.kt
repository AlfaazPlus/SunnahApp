package com.alfaazplus.sunnah.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.MainAppBar
import com.alfaazplus.sunnah.ui.components.hadith.HadithCollectionList
import com.alfaazplus.sunnah.ui.utils.keys.Routes

@Composable
fun HomeScreen() {
    val navController = LocalNavHostController.current

    Scaffold(
        topBar = { MainAppBar() },
    ) { innerPadding ->
        HadithCollectionList(
            modifier = Modifier.padding(innerPadding),
            onCollectionClick = { collectionId ->
                navController.navigate(route = Routes.BOOKS_INDEX.arg(collectionId))
            }
        )
    }
}