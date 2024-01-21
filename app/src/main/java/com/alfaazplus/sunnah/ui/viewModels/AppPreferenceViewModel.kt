package com.alfaazplus.sunnah.ui.viewModels

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alfaazplus.sunnah.ui.activities.base.BaseActivity
import com.alfaazplus.sunnah.ui.theme.colors.BaseColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemeBlueColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemeDefaultColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemeMonoColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemePurpleColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemeRedColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemeVioletColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemeYellowColors
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.Preferences
import com.alfaazplus.sunnah.ui.utils.ThemeUtils

class AppPreferenceViewModel : ViewModel() {
    var themeMode by mutableStateOf(
        Preferences.getString(Keys.THEME_MODE, ThemeUtils.THEME_DEFAULT)!!
    )
    var themeColor by mutableStateOf(
        Preferences.getString(Keys.THEME_COLOR, ThemeUtils.THEME_COLOR_DEFAULT)!!
    )
    var isDynamicColor by mutableStateOf(
        Preferences.getBoolean(Keys.THEME_DYNAMIC_COLOR, false)
    )

    @Composable
    fun isDarkTheme(): Boolean {
        return when (themeMode) {
            ThemeUtils.THEME_LIGHT -> false
            ThemeUtils.THEME_DARK -> true
            else -> isSystemInDarkTheme()
        }
    }

    @Composable
    fun getColorScheme(context: Context, isDarkTheme: Boolean = isDarkTheme()): ColorScheme {
        // Dynamic color is available on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isDynamicColor) {
            return if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        val preferredColor: BaseColors = when (themeColor) {
            ThemeUtils.THEME_COLOR_BLUE -> ThemeBlueColors()
            ThemeUtils.THEME_COLOR_RED -> ThemeRedColors()
            ThemeUtils.THEME_COLOR_PURPLE -> ThemePurpleColors()
            ThemeUtils.THEME_COLOR_MONO -> ThemeMonoColors()
            ThemeUtils.THEME_COLOR_VIOLET -> ThemeVioletColors()
            ThemeUtils.THEME_COLOR_YELLOW -> ThemeYellowColors()
            else -> ThemeDefaultColors()
        }

        return if (isDarkTheme) preferredColor.darkColors() else preferredColor.lightColors()
    }
}

@Composable
fun appPreferenceModel(): AppPreferenceViewModel {
    return viewModel(LocalContext.current as BaseActivity)
}