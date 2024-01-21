package com.alfaazplus.sunnah.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.components.common.SwitchItem
import com.alfaazplus.sunnah.ui.components.extended.fullWidthColumn
import com.alfaazplus.sunnah.ui.components.settings.ListItemCategoryLabel
import com.alfaazplus.sunnah.ui.components.settings.SettingsItem
import com.alfaazplus.sunnah.ui.components.settings.SettingsThemeItem
import com.alfaazplus.sunnah.ui.components.settings.ThemeSelectorSheet
import com.alfaazplus.sunnah.ui.models.theme.ThemeItem
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
import com.alfaazplus.sunnah.ui.viewModels.appPreferenceModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsThemeScreen() {
    val themeItems = listOf(
        ThemeItem(ThemeUtils.THEME_COLOR_DEFAULT, R.string.theme_default, ThemeDefaultColors()),
        ThemeItem(ThemeUtils.THEME_COLOR_BLUE, R.string.theme_blue, ThemeBlueColors()),
        ThemeItem(ThemeUtils.THEME_COLOR_RED, R.string.theme_red, ThemeRedColors()),
        ThemeItem(ThemeUtils.THEME_COLOR_PURPLE, R.string.theme_purple, ThemePurpleColors()),
        ThemeItem(ThemeUtils.THEME_COLOR_VIOLET, R.string.theme_violet, ThemeVioletColors()),
        ThemeItem(ThemeUtils.THEME_COLOR_YELLOW, R.string.theme_yellow, ThemeYellowColors()),
        ThemeItem(ThemeUtils.THEME_COLOR_MONO, R.string.theme_mono, ThemeMonoColors()),
    )

    val appPreferenceModel = appPreferenceModel()
    var showThemeBottomSheet by remember { mutableStateOf(false) }
    val themeSheetState = rememberModalBottomSheetState()

    val span = 2

    Scaffold(
        topBar = { AppBar(title = "App Theme") },
    ) { paddingValues ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(span),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 150.dp),
        ) {
            fullWidthColumn(span) {
                SettingsItem(
                    title = R.string.theme_mode,
                    subtitle = ThemeUtils.resolveThemeModeLabel(appPreferenceModel.themeMode),
                ) { showThemeBottomSheet = true }
            }
            if (ThemeUtils.isDynamicColorSupported()) {
                fullWidthColumn(span) {
                    SwitchItem(
                        title = R.string.dynamic_color,
                        subtitle = R.string.dynamic_color_description,
                        checked = appPreferenceModel.isDynamicColor,
                    ) {
                        appPreferenceModel.isDynamicColor = it
                        Preferences.edit { putBoolean(Keys.THEME_DYNAMIC_COLOR, it) }
                    }
                }
            }
            if (!ThemeUtils.isDynamicColorSupported() || !appPreferenceModel.isDynamicColor) {
                fullWidthColumn(span) { ListItemCategoryLabel(title = "Theme colors") }
                items(themeItems.size) { index ->
                    SettingsThemeItem(
                        themeItem = themeItems[index],
                        isDarkTheme = appPreferenceModel.isDarkTheme(),
                        currentThemeColor = appPreferenceModel.themeColor
                    ) {
                        appPreferenceModel.themeColor = it
                        Preferences.edit { putString(Keys.THEME_COLOR, it) }
                    }
                }
            }
        }

        if (showThemeBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showThemeBottomSheet = false },
                sheetState = themeSheetState,
            ) {
                ThemeSelectorSheet(
                    themeModel = appPreferenceModel,
                ) { showThemeBottomSheet = false }
            }
        }
    }
}