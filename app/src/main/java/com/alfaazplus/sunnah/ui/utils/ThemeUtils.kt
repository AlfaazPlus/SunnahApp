package com.alfaazplus.sunnah.ui.utils

import android.os.Build
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.Preferences

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
        val themeId = when (Preferences.getString(Keys.THEME_COLOR, THEME_COLOR_DEFAULT)) {
            THEME_COLOR_BLUE -> R.style.Theme_Blue
            THEME_COLOR_RED -> R.style.Theme_Red
            THEME_COLOR_PURPLE -> R.style.Theme_Purple
            THEME_COLOR_MONO -> R.style.Theme_Monochrome
            THEME_COLOR_VIOLET -> R.style.Theme_Violet
            THEME_COLOR_YELLOW -> R.style.Theme_Yellow
            else -> R.style.Theme_Base
        }

        return themeId
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
}
