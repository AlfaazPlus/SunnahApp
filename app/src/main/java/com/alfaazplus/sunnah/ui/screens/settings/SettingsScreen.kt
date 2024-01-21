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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.components.ListItem
import com.alfaazplus.sunnah.ui.components.ListItemIcon
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.components.common.SwitchItem
import com.alfaazplus.sunnah.ui.components.settings.HadithTextOptionsSheet
import com.alfaazplus.sunnah.ui.components.settings.ListItemCategoryLabel
import com.alfaazplus.sunnah.ui.components.settings.SettingsItem
import com.alfaazplus.sunnah.ui.components.settings.TextSizeSheet
import com.alfaazplus.sunnah.ui.helpers.NavigationHelper
import com.alfaazplus.sunnah.ui.utils.ReaderUtils
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.utils.ThemeUtils
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.rememberPreference
import com.alfaazplus.sunnah.ui.viewModels.appPreferenceModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val navController = LocalNavHostController.current
    val context = LocalContext.current
    val appPreferenceModel = appPreferenceModel()

    var selectedHadithTextOption by rememberPreference(key = Keys.HADITH_TEXT_OPTION, defaultValue = ReaderUtils.HADITH_TEXT_OPTION_BOTH)
    var showSanad by rememberPreference(key = Keys.SHOW_SANAD, defaultValue = true)

    var showTextSizesSheet by remember { mutableStateOf(false) }
    val textSizesSheetState = rememberModalBottomSheetState(true)

    var showHadithTextOptionsSheet by remember { mutableStateOf(false) }
    val hadithTextOptionsSheetState = rememberModalBottomSheetState(true)

    Scaffold(
        topBar = { AppBar(title = "Settings") }
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 150.dp),
        ) {
            ListItemCategoryLabel(title = "App Settings")
            SettingsItem(
                title = R.string.app_theme,
                subtitle = ThemeUtils.resolveThemeModeLabel(appPreferenceModel.themeMode),
                icon = R.drawable.ic_theme,
            ) { navController.navigate(Routes.SETTINGS_THEME) }
            ListItemCategoryLabel(title = "Reader Settings")
            SettingsItem(
                title = R.string.text_sizes,
                icon = R.drawable.ic_text_size,
            ) { showTextSizesSheet = true }
            SettingsItem(
                title = R.string.hadith_text_option,
                subtitle = ReaderUtils.resolveHadithTextOptionLabel(selectedHadithTextOption),
                icon = R.drawable.ic_hadith_text_option,
            ) { showHadithTextOptionsSheet = true }
            SwitchItem(
                title = R.string.show_sanad,
                subtitle = R.string.show_sanad_description,
                checked = showSanad,
            ) { showSanad = it }
            ListItemCategoryLabel(title = "Other")
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
                leading = { ListItemIcon(R.drawable.ic_privacy_policy) },
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

            if (showTextSizesSheet) {
                TextSizeSheet(textSizesSheetState) {
                    showTextSizesSheet = false
                }
            }

            if (showHadithTextOptionsSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showHadithTextOptionsSheet = false },
                    sheetState = hadithTextOptionsSheetState,
                ) {
                    HadithTextOptionsSheet(selectedHadithTextOption) {
                        selectedHadithTextOption = it
                        showHadithTextOptionsSheet = false
                    }
                }
            }
        }
    }
}