package com.alfaazplus.sunnah.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.BuildConfig
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.api.DownloadSourceUtils
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.ListItem
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.components.common.SwitchItem
import com.alfaazplus.sunnah.ui.components.settings.DailyReminderSheet
import com.alfaazplus.sunnah.ui.components.settings.HadithTextOptionsSheet
import com.alfaazplus.sunnah.ui.components.settings.LayoutOptionSheet
import com.alfaazplus.sunnah.ui.components.settings.ListItemCategoryLabel
import com.alfaazplus.sunnah.ui.components.settings.ResourceDownloadSrcSheet
import com.alfaazplus.sunnah.ui.components.settings.SettingsItem
import com.alfaazplus.sunnah.ui.components.settings.TextSizeSheet
import com.alfaazplus.sunnah.ui.helpers.NavigationHelper
import com.alfaazplus.sunnah.ui.utils.AppUtils
import com.alfaazplus.sunnah.ui.utils.ThemeUtils
import com.alfaazplus.sunnah.ui.utils.extension.copyToClipboard
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.utils.message.MessageUtils
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences.KEY_IS_SANAD_ENABLED
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import com.alfaazplus.sunnah.ui.utils.shared_preference.SPAppConfigs
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    showReaderSettingsOnly: Boolean = false,
) {
    val navController = LocalNavHostController.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val themeMode = ThemeUtils.observeThemeMode()

    var showDailyReminderSheet by remember { mutableStateOf(false) }
    var showLayoutOptionSheet by remember { mutableStateOf(false) }
    var showTextSizesSheet by remember { mutableStateOf(false) }
    var showHadithTextOptionsSheet by remember { mutableStateOf(false) }
    var showResourceDownloadSrcSheet by remember { mutableStateOf(false) }

    val availableLocales = availableAppLocales()

    val selectedLanguage = remember(context, availableLocales) {
        val selectedLocale = SPAppConfigs
            .getLocale(context)
            .let { languageCode ->
                availableLocales.firstOrNull { it.first == languageCode }
            }

        selectedLocale?.second ?: ""
    }

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
                    title = R.string.app_langauge,
                    subtitleStr = selectedLanguage,
                    icon = R.drawable.ic_language,
                ) { navController.navigate(Routes.SETTINGS_LANGUAGE) }

                SettingsItem(
                    title = R.string.app_theme,
                    subtitle = ThemeUtils.resolveThemeModeLabel(themeMode),
                    icon = R.drawable.ic_theme,
                ) { navController.navigate(Routes.SETTINGS_THEME) }

                SettingsItem(
                    title = R.string.daily_reminder,
                    subtitle = AppUtils.observeDailyReminderEnabled(),
                    icon = R.drawable.bell,
                ) { showDailyReminderSheet = true }
            }

            ListItemCategoryLabel(title = stringResource(R.string.reader_settings))

            SettingsItem(
                title = R.string.selectTranslation,
                subtitleStr = TranslationUtils.resolveHadithTranslationLabel(),
                icon = R.drawable.ic_translations,
            ) { navController.navigate(Routes.SETTINGS_TRANSLATIONS) }

            SettingsItem(
                title = R.string.hadith_layout,
                icon = R.drawable.ic_square_menu,
                subtitle = ReaderPreferences.resolveHadithLayoutLabel(),
            ) { showLayoutOptionSheet = true }

            SettingsItem(
                title = R.string.text_size_and_style,
                icon = R.drawable.ic_text_size,
            ) { showTextSizesSheet = true }

            SettingsItem(
                title = R.string.hadith_text_option,
                subtitle = ReaderPreferences.resolveHadithTextOptionLabel(),
                icon = R.drawable.ic_hadith_text_option,
            ) { showHadithTextOptionsSheet = true }

            SwitchItem(
                title = R.string.show_sanad,
                subtitle = R.string.show_sanad_description,
                checked = ReaderPreferences.observeIsSanadEnabled(),
            ) {
                coroutineScope.launch {
                    DataStoreManager.write(KEY_IS_SANAD_ENABLED, it)
                }
            }

            if (!showReaderSettingsOnly) {
                ListItemCategoryLabel(title = stringResource(R.string.other_settings))

                SettingsItem(
                    title = R.string.resource_download_source,
                    icon = R.drawable.ic_download,
                    subtitleStr = DownloadSourceUtils.observeCurrentSourceName(),
                ) {
                    showResourceDownloadSrcSheet = true
                }

                SettingsItem(
                    title = R.string.app_logs,
                    icon = R.drawable.bug,
                    subtitle = R.string.app_logs_desc,
                ) {
                    navController.navigate(Routes.APP_LOGS)
                }

                ListItem(
                    title = R.string.github,
                    subtitle = R.string.github_description,
                    leading = {
                        Image(
                            painter = painterResource(R.drawable.ic_github),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    },
                ) { NavigationHelper.openGithubRepo(context) }

                ListItem(
                    title = R.string.privacy_policy,
                    leading = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_shield),
                            contentDescription = null,
                        )
                    },
                ) { NavigationHelper.openPrivacyPolicy(context) }

                ListItem(
                    title = R.string.about_us,
                    leading = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = null,
                        )
                    },
                ) { NavigationHelper.openAboutUs(context) }

                ListItem(
                    title = R.string.rate_app,
                    leading = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = null,
                        )
                    },
                ) { NavigationHelper.openPlayStoreListing(context) }

                ListItem(
                    title = R.string.share_app,
                    leading = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_share),
                            contentDescription = null,
                        )
                    },
                ) {
                    NavigationHelper.shareApp(context)
                }

                ListItem(
                    title = R.string.install_quranapp,
                    subtitle = R.string.install_quranapp_description_short,
                    leading = {
                        Image(
                            painter = painterResource(R.drawable.logo_quranapp),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    },
                ) { NavigationHelper.openQuranAppPlayStoreListing(context) }

                ListItem(
                    title = R.string.donate,
                    leading = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_donate),
                            contentDescription = null,
                            tint = colorScheme.primary,
                        )
                    },
                ) { NavigationHelper.openDonationPage(context) }

                AppVersionFooter()
            }

            DailyReminderSheet(
                isOpen = showDailyReminderSheet,
                onClose = {
                    showDailyReminderSheet = false
                },
            )

            LayoutOptionSheet(isOpen = showLayoutOptionSheet) {
                showLayoutOptionSheet = false
            }

            TextSizeSheet(isOpen = showTextSizesSheet) {
                showTextSizesSheet = false
            }

            HadithTextOptionsSheet(isOpen = showHadithTextOptionsSheet) {
                showHadithTextOptionsSheet = false
            }

            ResourceDownloadSrcSheet(isOpen = showResourceDownloadSrcSheet) {
                showResourceDownloadSrcSheet = false
            }
        }
    }
}

@Composable
private fun AppVersionFooter() {
    val context = LocalContext.current
    val resources = LocalResources.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                context.copyToClipboard(BuildConfig.VERSION_NAME)
                MessageUtils.showClipboardMessage(context, resources.getString(R.string.copied_to_clipboard))
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        Text(
            text = BuildConfig.VERSION_NAME,
            style = MaterialTheme.typography.labelMedium,
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_clipboard),
            contentDescription = null,
            modifier = Modifier
                .height(16.dp)
                .width(16.dp),
        )
    }
}
