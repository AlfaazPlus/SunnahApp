package com.alfaazplus.sunnah.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alfaazplus.sunnah.ui.components.LastCrashReportDialog
import com.alfaazplus.sunnah.ui.components.appbars.MainBottomNavigationBar
import com.alfaazplus.sunnah.ui.components.reader.SettingUpOverlay
import com.alfaazplus.sunnah.ui.enterTransition
import com.alfaazplus.sunnah.ui.exitTransition
import com.alfaazplus.sunnah.ui.popEnterTransition
import com.alfaazplus.sunnah.ui.popExitTransition
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.viewModels.HadithSetupViewModel

@Composable
private fun Content() {
    val navController = rememberNavController()

    Column {
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.weight(1f),
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition },
        ) {
            composable(Routes.HOME) { HomeScreen() }
            composable(Routes.LIBRARY) { LibraryScreen() }
            composable(Routes.SEARCH) { SearchScreen(withBackButton = false) }
        }

        MainBottomNavigationBar(navController)
    }
}

@Composable
fun MainScreen(
    setupVm: HadithSetupViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val isSettingUp by setupVm.isSettingUp.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        setupVm.initializeHadiths(context)
    }

    if (isSettingUp) {
        SettingUpOverlay()
    } else {
        Content()
    }

    LastCrashReportDialog()
}
