package com.alfaazplus.sunnah.ui.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.hadith.HadithCollectionList
import com.alfaazplus.sunnah.ui.utils.keys.Routes

@Composable
fun HomeScreen() {
    val navController = LocalNavHostController.current

    HadithCollectionList(
        modifier = Modifier,
        onCollectionClick = { collectionId ->
            navController.navigate(route = Routes.BOOKS_INDEX.arg(collectionId))
        }
    )
}