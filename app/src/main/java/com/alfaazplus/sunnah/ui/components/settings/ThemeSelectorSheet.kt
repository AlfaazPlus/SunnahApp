package com.alfaazplus.sunnah.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.RadioItem
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.Preferences
import com.alfaazplus.sunnah.ui.utils.ThemeUtils
import com.alfaazplus.sunnah.ui.viewModels.AppPreferenceViewModel

@Composable
fun ThemeSelectorSheet(themeModel: AppPreferenceViewModel, onDismiss: () -> Unit) {
    val items = listOf(
        Triple(ThemeUtils.THEME_DEFAULT, R.string.system_default, R.string.theme_default_description),
        Triple(ThemeUtils.THEME_DARK, R.string.dark, R.string.theme_dark_description),
        Triple(ThemeUtils.THEME_LIGHT, R.string.light, null),
    )

    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        items.forEach { (theme, title, description) ->
            RadioItem(
                title = title,
                subtitle = description,
                selected = themeModel.themeMode == theme,
                onClick = {
                    themeModel.themeMode = theme
                    Preferences.edit { putString(Keys.THEME_MODE, theme) }
                    onDismiss()
                }
            )
        }
    }
}