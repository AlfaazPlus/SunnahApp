package com.alfaazplus.sunnah.ui.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.RadioItem
import com.alfaazplus.sunnah.ui.components.dialogs.BottomSheet
import com.alfaazplus.sunnah.ui.utils.ThemeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ThemeSelectorSheet(isOpen: Boolean, onDismiss: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val themeMode = ThemeUtils.getThemeMode()

    val items = listOf(
        Triple(ThemeUtils.THEME_DEFAULT, R.string.system_default, R.string.theme_default_description),
        Triple(ThemeUtils.THEME_DARK, R.string.dark, R.string.theme_dark_description),
        Triple(ThemeUtils.THEME_LIGHT, R.string.light, null),
    )

    BottomSheet(
        isOpen = isOpen,
        onDismiss = onDismiss,
        icon = R.drawable.ic_theme,
        title = stringResource(R.string.app_theme),
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            items.forEach { (theme, title, description) ->
                RadioItem(
                    title = title,
                    subtitle = description,
                    selected = themeMode == theme,
                    onClick = {
                        coroutineScope.launch {
                            ThemeUtils.setThemeMode(theme)

                            withContext(Dispatchers.Main) {
                                onDismiss()
                            }
                        }
                    }
                )
            }
        }
    }
}