package com.alfaazplus.sunnah.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alfaazplus.sunnah.R
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
    var titleRes by remember { mutableStateOf<Int?>(R.string.app_name) }
    var showAppbarActions by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            titleRes = when (destination.route) {
                Routes.LIBRARY -> R.string.library
                Routes.SEARCH -> R.string.global_search
                else -> null
            }

            showAppbarActions = when (destination.route) {
                Routes.SEARCH -> false
                else -> true
            }
        }
    }

    Scaffold(
        topBar = { MainAppBar(titleRes, showAppbarActions) },
        bottomBar = { MainBottomNavigationBar(navController) },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition },
        ) {
            composable(Routes.HOME) { HomeScreen() }
            composable(Routes.LIBRARY) { LibraryScreen() }
            composable(Routes.SEARCH) { SearchScreen() }
        }
    }
}