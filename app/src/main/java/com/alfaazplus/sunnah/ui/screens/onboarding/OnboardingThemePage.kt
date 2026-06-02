package com.alfaazplus.sunnah.ui.screens.onboarding

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.RadioItem
import com.alfaazplus.sunnah.ui.components.common.SwitchItem
import com.alfaazplus.sunnah.ui.components.settings.ListItemCategoryLabel
import com.alfaazplus.sunnah.ui.components.settings.SettingsThemeItem
import com.alfaazplus.sunnah.ui.models.theme.ThemeItem
import com.alfaazplus.sunnah.ui.theme.colors.ThemeBlueColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemeDefaultColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemeMonoColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemePurpleColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemeRedColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemeVioletColors
import com.alfaazplus.sunnah.ui.theme.colors.ThemeYellowColors
import com.alfaazplus.sunnah.ui.utils.ThemeUtils
import com.alfaazplus.sunnah.ui.utils.extension.verticalFadingEdge
import kotlinx.coroutines.launch

@Composable
fun OnboardingThemePage() {
    val themeItems = listOf(
        ThemeItem(ThemeUtils.THEME_COLOR_DEFAULT, R.string.theme_default, ThemeDefaultColors()),
        ThemeItem(ThemeUtils.THEME_COLOR_BLUE, R.string.theme_blue, ThemeBlueColors()),
        ThemeItem(ThemeUtils.THEME_COLOR_RED, R.string.theme_red, ThemeRedColors()),
        ThemeItem(ThemeUtils.THEME_COLOR_PURPLE, R.string.theme_purple, ThemePurpleColors()),
        ThemeItem(ThemeUtils.THEME_COLOR_VIOLET, R.string.theme_violet, ThemeVioletColors()),
        ThemeItem(ThemeUtils.THEME_COLOR_YELLOW, R.string.theme_yellow, ThemeYellowColors()),
        ThemeItem(ThemeUtils.THEME_COLOR_MONO, R.string.theme_mono, ThemeMonoColors()),
    )

    val themeMode = ThemeUtils.observeThemeMode()
    val isDarkTheme = ThemeUtils.observeDarkTheme()
    val themeColor = ThemeUtils.observeThemeColor()
    val isDynamicColor = ThemeUtils.observeIsDynamicColor()
    val scope = rememberCoroutineScope()

    val modeItems = listOf(
        Triple(ThemeUtils.THEME_MODE_DEFAULT, R.string.system_default, R.string.theme_default_description),
        Triple(ThemeUtils.THEME_MODE_DARK, R.string.dark, R.string.theme_dark_description),
        Triple(ThemeUtils.THEME_MODE_LIGHT, R.string.light, null),
    )

    val scrollState = rememberScrollState()
    val dynamicColorSupported = ThemeUtils.isDynamicColorSupported()

    Box(Modifier.verticalFadingEdge(scrollState)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(8.dp),
        ) {
            modeItems.forEach { (mode, title, description) ->
                RadioItem(
                    title = title,
                    subtitle = description,
                    selected = themeMode == mode,
                    onClick = {
                        scope.launch {
                            ThemeUtils.setThemeMode(mode)
                            AppCompatDelegate.setDefaultNightMode(
                                ThemeUtils.resolveThemeModeForDelegate(mode),
                            )
                        }
                    },
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (dynamicColorSupported) {
                    SwitchItem(
                        title = R.string.dynamic_color,
                        subtitle = R.string.dynamic_color_description,
                        checked = isDynamicColor,
                    ) {
                        scope.launch {
                            ThemeUtils.setDynamicColor(it)
                        }
                    }
                }

                if (!dynamicColorSupported || !isDynamicColor) {
                    ListItemCategoryLabel(title = stringResource(R.string.theme_colors))

                    themeItems.chunked(3).forEach { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            rowItems.forEach { item ->
                                Box(modifier = Modifier.weight(1f)) {
                                    SettingsThemeItem(
                                        themeItem = item,
                                        isDarkTheme = isDarkTheme,
                                        currentThemeColor = themeColor,
                                    ) {
                                        scope.launch {
                                            ThemeUtils.setThemeColor(it)
                                        }
                                    }
                                }
                            }

                            repeat(3 - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
