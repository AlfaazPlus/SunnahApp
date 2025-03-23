package com.alfaazplus.sunnah.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.ListItem
import com.alfaazplus.sunnah.ui.components.ListItemIcon
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.components.common.SwitchItem
import com.alfaazplus.sunnah.ui.components.settings.HadithTextOptionsSheet
import com.alfaazplus.sunnah.ui.components.settings.LayoutOptionSheet
import com.alfaazplus.sunnah.ui.components.settings.ListItemCategoryLabel
import com.alfaazplus.sunnah.ui.components.settings.SettingsItem
import com.alfaazplus.sunnah.ui.components.settings.TextSizeSheet
import com.alfaazplus.sunnah.ui.helpers.NavigationHelper
import com.alfaazplus.sunnah.ui.utils.ReaderUtils
import com.alfaazplus.sunnah.ui.utils.ThemeUtils
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    showReaderSettingsOnly: Boolean = false,
) {
    val navController = LocalNavHostController.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val themeMode = ThemeUtils.getThemeMode()

    var showLayoutOptionSheet by remember { mutableStateOf(false) }
    var showTextSizesSheet by remember { mutableStateOf(false) }
    var showHadithTextOptionsSheet by remember { mutableStateOf(false) }

    Scaffold(topBar = { AppBar(title = stringResource(R.string.settings)) }) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 150.dp),
        ) {
            if (!showReaderSettingsOnly) {
                ListItemCategoryLabel(title = stringResource(R.string.app_settings))
                SettingsItem(
                    title = R.string.app_theme,
                    subtitle = ThemeUtils.resolveThemeModeLabel(themeMode),
                    icon = R.drawable.ic_theme,
                ) { navController.navigate(Routes.SETTINGS_THEME) }
            }

            ListItemCategoryLabel(title = stringResource(R.string.reader_settings))
            SettingsItem(
                title = R.string.hadith_layout,
                icon = R.drawable.ic_square_menu,
                subtitle = ReaderUtils.resolveHadithLayoutLabel(),
            ) { showLayoutOptionSheet = true }
            SettingsItem(
                title = R.string.text_sizes,
                icon = R.drawable.ic_text_size,
            ) { showTextSizesSheet = true }
            SettingsItem(
                title = R.string.hadith_text_option,
                subtitle = ReaderUtils.resolveHadithTextOptionLabel(),
                icon = R.drawable.ic_hadith_text_option,
            ) { showHadithTextOptionsSheet = true }
            SwitchItem(
                title = R.string.show_sanad,
                subtitle = R.string.show_sanad_description,
                checked = ReaderUtils.getIsSanadEnabled(),
            ) {
                coroutineScope.launch {
                    DataStoreManager.write(booleanPreferencesKey(Keys.SHOW_SANAD), it)
                }
            }

            if (!showReaderSettingsOnly) {
                ListItemCategoryLabel(title = stringResource(R.string.other_settings))
                ListItem(
                    title = R.string.github,
                    subtitle = R.string.github_description,
                    leading = {
                        Image(
                            painter = painterResource(R.drawable.ic_github),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 15.dp)
                                .height(24.dp)
                                .width(24.dp),
                        )
                    },
                ) { NavigationHelper.openGithubRepo(context) }
                ListItem(
                    title = R.string.privacy_policy,
                    leading = { ListItemIcon(R.drawable.ic_shield) },
                ) { NavigationHelper.openPrivacyPolicy(context) }
                ListItem(
                    title = R.string.about_us,
                    leading = { ListItemIcon(R.drawable.ic_info) },
                ) { NavigationHelper.openAboutUs(context) }
                ListItem(
                    title = R.string.rate_app,
                    leading = { ListItemIcon(R.drawable.ic_star) },
                ) { NavigationHelper.openPlayStoreListing(context) }
                ListItem(
                    title = R.string.share_app,
                    leading = { ListItemIcon(R.drawable.ic_share) },
                ) { }
                ListItem(
                    title = R.string.install_quranapp,
                    subtitle = R.string.install_quranapp_description,
                    leading = {
                        Image(
                            painter = painterResource(R.drawable.logo_quranapp),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 15.dp)
                                .height(24.dp)
                                .width(24.dp),
                        )
                    },
                ) { NavigationHelper.openQuranAppPlayStoreListing(context) }
            }

            LayoutOptionSheet(isOpen = showLayoutOptionSheet) {
                showLayoutOptionSheet = false
            }

            TextSizeSheet(isOpen = showTextSizesSheet) {
                showTextSizesSheet = false
            }

            HadithTextOptionsSheet(
                isOpen = showHadithTextOptionsSheet,
            ) {
                showHadithTextOptionsSheet = false
            }
        }
    }
}