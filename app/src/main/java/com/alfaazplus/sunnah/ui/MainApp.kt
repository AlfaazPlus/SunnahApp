package com.alfaazplus.sunnah.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alfaazplus.sunnah.ui.screens.BooksIndexScreen
import com.alfaazplus.sunnah.ui.screens.ReaderScreen
import com.alfaazplus.sunnah.ui.screens.main.HistoryScreen
import com.alfaazplus.sunnah.ui.screens.main.HomeScreen
import com.alfaazplus.sunnah.ui.screens.settings.SettingsLanguageScreen
import com.alfaazplus.sunnah.ui.screens.settings.SettingsManageCollectionsScreen
import com.alfaazplus.sunnah.ui.screens.settings.SettingsScreen
import com.alfaazplus.sunnah.ui.screens.settings.SettingsThemeScreen
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.keys.Routes

val enterTransition = slideInHorizontally(
    initialOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(durationMillis = 100)
)
val exitTransition = slideOutHorizontally(
    targetOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(durationMillis = 100)
)
val popEnterTransition = slideInHorizontally(
    initialOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(durationMillis = 100)
)
val popExitTransition = slideOutHorizontally(
    targetOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(durationMillis = 100)
)

private fun NavGraphBuilder.route(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = { enterTransition },
        exitTransition = { exitTransition },
        popEnterTransition = { popEnterTransition },
        popExitTransition = { popExitTransition },
        content = content
    )
}

@Composable
fun MainApp() {
    val navController = rememberNavController()

    CompositionLocalProvider(LocalNavHostController provides navController) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            NavHost(
                navController = navController,
                startDestination = Routes.READER(),
            ) {
                route(Routes.HOME) { HomeScreen() }
                route(Routes.HISTORY) { HistoryScreen() }
                route(Routes.SETTINGS) { SettingsScreen() }
                route(Routes.SETTINGS_THEME) { SettingsThemeScreen() }
                route(Routes.SETTINGS_MANAGE_COLLECTIONS) { SettingsManageCollectionsScreen() }

                route(
                    Routes.READER(),
                    arguments = listOf(
                        navArgument(Keys.COLLECTION_ID) { type = NavType.IntType },
                        navArgument(Keys.BOOK_ID) { type = NavType.IntType },
                        navArgument(Keys.HADITH_NUMBER) {
                            type = NavType.StringType
                            defaultValue = null
                            nullable = true
                        }
                    )
                ) { rsEntry ->
                    ReaderScreen(
                        collectionId = rsEntry.arguments?.getInt(Keys.COLLECTION_ID, 1) ?: 1,
                        bookId = rsEntry.arguments?.getInt(Keys.BOOK_ID, 1) ?: 1,
                        hadithNumber = rsEntry.arguments?.getString(Keys.HADITH_NUMBER)
                    )
                }
                route(
                    Routes.BOOKS_INDEX(),
                    arguments = listOf(navArgument(Keys.COLLECTION_ID) { type = NavType.IntType })
                ) { bsEntry ->
                    BooksIndexScreen(collectionId = bsEntry.arguments?.getInt(Keys.COLLECTION_ID) ?: 0)
                }
            }
        }
    }
}