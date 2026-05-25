package com.alfaazplus.sunnah.ui.screens.settings


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.AppBar
import com.alfaazplus.sunnah.ui.theme.alpha
import com.alfaazplus.sunnah.ui.utils.app.LocalAppLocale
import com.alfaazplus.sunnah.ui.utils.app.NumeralSystem
import com.alfaazplus.sunnah.ui.utils.app.appLocaleForLanguageChange
import com.alfaazplus.sunnah.ui.utils.app.normalizedLanguageTag
import com.alfaazplus.sunnah.ui.utils.app.setAppLocale
import com.alfaazplus.sunnah.ui.utils.shared_preference.SPAppConfigs
import java.util.Locale

data class LanguageModel(
    val rawLanguageTag: String,
    val language: String,
    val localizedName: String,
    val nativeName: String,
)

@Composable
fun availableAppLocales(): Set<Pair<String, String>> {
    return setOf(
        SPAppConfigs.LOCALE_DEFAULT to stringResource(R.string.system_default),
        "en" to "English",
        "ar" to "العربية",
        "ur" to "اردو",
    )
}

@Composable
fun SettingsLanguageScreen() {
    val context = LocalContext.current
    val platformLocale = LocalAppLocale.current.platformLocale
    val availableLocales = availableAppLocales()

    val languages = remember(platformLocale) {
        availableLocales.map { (rawLanguageTag, nativeName) ->
            val locale = if (rawLanguageTag == SPAppConfigs.LOCALE_DEFAULT) {
                null
            } else {
                Locale.forLanguageTag(rawLanguageTag.normalizedLanguageTag())
            }

            val localizedName = locale
                ?.getDisplayName(platformLocale)
                ?.replaceFirstChar { it.uppercase() } ?: nativeName

            LanguageModel(
                rawLanguageTag,
                locale?.language ?: "",
                localizedName,
                nativeName,
            )
        }
    }


    var committed by remember {
        mutableStateOf(
            SPAppConfigs.getLocale(context)
        )
    }

    var searchQuery by remember { mutableStateOf("") }

    val filteredLanguages by remember(searchQuery, languages) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                languages
            } else {
                val query = searchQuery
                    .trim()
                    .lowercase()
                languages.filter {
                    it.localizedName
                        .lowercase()
                        .contains(query) || it.nativeName
                        .lowercase()
                        .contains(query) || it.rawLanguageTag
                        .lowercase()
                        .contains(query)
                }
            }
        }
    }

    fun save(selectedTag: String) {
        val applied = appLocaleForLanguageChange(context, selectedTag, NumeralSystem.LATN)

        setAppLocale(context, applied)

        committed = applied.rawLanguageTag
    }

    val selectedTag = committed

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        topBar = {
            AppBar(
                stringResource(R.string.app_langauge),
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                top = 16.dp, bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (filteredLanguages.isNotEmpty()) {
                items(
                    filteredLanguages,
                    key = { it.rawLanguageTag },
                ) { model ->
                    val isSelected = model.rawLanguageTag == selectedTag
                    Column(Modifier.fillMaxWidth()) {
                        LanguageItem(
                            language = model,
                            isSelected = isSelected,
                            onSelect = {
                                save(model.rawLanguageTag)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageItem(
    language: LanguageModel,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onSelect
            )
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RadioButton(
            selected = isSelected, onClick = onSelect, colors = RadioButtonDefaults.colors(
                selectedColor = colorScheme.primary, unselectedColor = colorScheme.onSurfaceVariant.alpha(0.4f)
            ), modifier = Modifier.size(20.dp)
        )

        Column(
            modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = language.localizedName, style = typography.labelLarge, color = if (isSelected) colorScheme.primary else colorScheme.onSurface
            )

            if (language.nativeName != language.localizedName) {
                Text(
                    text = language.nativeName,
                    style = typography.bodyMedium,
                    color = (if (isSelected) colorScheme.primary else colorScheme.onSurface).alpha(0.7f),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

