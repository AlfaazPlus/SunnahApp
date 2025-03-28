package com.alfaazplus.sunnah.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.HadithOfTheDay
import com.alfaazplus.sunnah.ui.components.hadith.HadithCollectionList
import com.alfaazplus.sunnah.ui.utils.keys.Routes

@Composable
fun HomeScreen() {
    val navController = LocalNavHostController.current

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HadithOfTheDay()
        HadithCollectionList(modifier = Modifier.heightIn(max = 1000.dp), onCollectionClick = { collectionId ->
            navController.navigate(route = Routes.BOOKS_INDEX.arg(collectionId))
        })
    }
}