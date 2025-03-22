package com.alfaazplus.sunnah.ui.utils

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.theme.colors.BaseColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemeBlueColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemeDefaultColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemeMonoColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemePurpleColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemeRedColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemeVioletColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemeYellowColors
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager

object ThemeUtils {
    const val THEME_DEFAULT = "default"
    const val THEME_LIGHT = "light"
    const val THEME_DARK = "dark"

    const val THEME_COLOR_DEFAULT = "default"
    const val THEME_COLOR_BLUE = "blue"
    const val THEME_COLOR_RED = "red"
    const val THEME_COLOR_PURPLE = "purple"
    const val THEME_COLOR_MONO = "mono"
    const val THEME_COLOR_VIOLET = "violet"
    const val THEME_COLOR_YELLOW = "yellow"

    fun getThemeId(): Int {
        /* val themeId = when (DataStoreManager.read(stringPreferencesKey(Keys.THEME_COLOR), THEME_COLOR_DEFAULT)) {
             THEME_COLOR_BLUE -> R.style.Theme_Blue
             THEME_COLOR_RED -> R.style.Theme_Red
             THEME_COLOR_PURPLE -> R.style.Theme_Purple
             THEME_COLOR_MONO -> R.style.Theme_Monochrome
             THEME_COLOR_VIOLET -> R.style.Theme_Violet
             THEME_COLOR_YELLOW -> R.style.Theme_Yellow
             else -> R.style.Theme_Base
         }*/

        return R.style.Theme_Base
    }

    fun isDynamicColorSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    fun resolveThemeModeLabel(themeMode: String): Int {
        return when (themeMode) {
            THEME_LIGHT -> R.string.light
            THEME_DARK -> R.string.dark
            else -> R.string.system_default
        }
    }

    @Composable
    fun isDarkTheme(): Boolean {
        val themeMode = getThemeMode()

        return when (themeMode) {
            THEME_LIGHT -> false
            THEME_DARK -> true
            else -> isSystemInDarkTheme()
        }
    }

    @Composable
    fun getThemeMode(): String {
        return DataStoreManager.observe(stringPreferencesKey(Keys.THEME_MODE), THEME_DEFAULT)
    }

    suspend fun setThemeMode(themeMode: String) {
        DataStoreManager.write(stringPreferencesKey(Keys.THEME_MODE), themeMode)
    }

    @Composable
    fun getThemeColor(): String {
        return DataStoreManager.observe(stringPreferencesKey(Keys.THEME_COLOR), THEME_COLOR_DEFAULT)
    }

    suspend fun setThemeColor(themeColor: String) {
        DataStoreManager.write(stringPreferencesKey(Keys.THEME_COLOR), themeColor)
    }

    @Composable
    fun isDynamicColor(): Boolean {
        return DataStoreManager.observe(booleanPreferencesKey(Keys.THEME_DYNAMIC_COLOR), false)
    }

    suspend fun setDynamicColor(isDynamicColor: Boolean) {
        DataStoreManager.write(booleanPreferencesKey(Keys.THEME_DYNAMIC_COLOR), isDynamicColor)
    }

    @Composable
    fun getColorScheme(context: Context, isDarkTheme: Boolean = isDarkTheme()): ColorScheme {
        val themeColor = getThemeColor()
        val isDynamicColor = isDynamicColor()

        // Dynamic color is available on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isDynamicColor) {
            return if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        val preferredColor: BaseColors = when (themeColor) {
            THEME_COLOR_BLUE -> ThemeBlueColors()
            THEME_COLOR_RED -> ThemeRedColors()
            THEME_COLOR_PURPLE -> ThemePurpleColors()
            THEME_COLOR_MONO -> ThemeMonoColors()
            THEME_COLOR_VIOLET -> ThemeVioletColors()
            THEME_COLOR_YELLOW -> ThemeYellowColors()
            else -> ThemeDefaultColors()
        }

        return if (isDarkTheme) preferredColor.darkColors() else preferredColor.lightColors()
    }
}
