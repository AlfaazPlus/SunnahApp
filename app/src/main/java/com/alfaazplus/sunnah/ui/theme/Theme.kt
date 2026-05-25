package com.alfaazplus.sunnah.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.ui.utils.ThemeUtils
import com.alfaazplus.sunnah.ui.utils.app.LocalAppLocale
import com.alfaazplus.sunnah.ui.utils.app.appLocaleFlow


@Composable
fun SunnahAppTheme(
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val view = LocalView.current

    val isDarkTheme = ThemeUtils.observeDarkTheme()
    val colorScheme = ThemeUtils.observeColorScheme(context, isDarkTheme)
    val appLocale by appLocaleFlow.collectAsState()

    if (!view.isInEditMode) {
        DisposableEffect(isDarkTheme) {
            val window = (view.context as Activity).window

            WindowCompat
                .getInsetsController(window, view)
                .apply {
                    isAppearanceLightStatusBars = !isDarkTheme
                    isAppearanceLightNavigationBars = !isDarkTheme
                }

            onDispose { }
        }
    }

    CompositionLocalProvider(LocalAppLocale provides appLocale) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = getAppTypography(),
            content = content,
        )
    }
}
