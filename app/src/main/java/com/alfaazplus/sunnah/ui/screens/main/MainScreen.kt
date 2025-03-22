package com.alfaazplus.sunnah.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alfaazplus.sunnah.ui.components.appbars.MainAppBar
import com.alfaazplus.sunnah.ui.components.appbars.MainBottomNavigationBar
import com.alfaazplus.sunnah.ui.enterTransition
import com.alfaazplus.sunnah.ui.exitTransition
import com.alfaazplus.sunnah.ui.popEnterTransition
import com.alfaazplus.sunnah.ui.popExitTransition
import com.alfaazplus.sunnah.ui.utils.keys.Routes

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        topBar = { MainAppBar() },
        bottomBar = { MainBottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            Modifier.padding(innerPadding),
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition },
        ) {
            composable(Routes.HOME) { HomeScreen() }
            composable(Routes.HISTORY) { HistoryScreen() }
            composable(Routes.SEARCH) { SearchScreen() }
        }
    }
}