package com.alfaazplus.sunnah.ui.screens.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.ui.components.common.RadioItem
import com.alfaazplus.sunnah.ui.screens.settings.LanguageModel
import com.alfaazplus.sunnah.ui.screens.settings.availableAppLocales
import com.alfaazplus.sunnah.ui.utils.app.LocalAppLocale
import com.alfaazplus.sunnah.ui.utils.app.NumeralSystem
import com.alfaazplus.sunnah.ui.utils.app.appLocaleForLanguageChange
import com.alfaazplus.sunnah.ui.utils.app.normalizedLanguageTag
import com.alfaazplus.sunnah.ui.utils.app.setAppLocale
import com.alfaazplus.sunnah.ui.utils.extension.verticalFadingEdge
import com.alfaazplus.sunnah.ui.utils.shared_preference.SPAppConfigs
import java.util.Locale

@Composable
fun OnboardingLanguagePage() {
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
        mutableStateOf(SPAppConfigs.getLocale(context))
    }

    fun save(selectedTag: String) {
        val applied = appLocaleForLanguageChange(context, selectedTag, NumeralSystem.LATN)
        setAppLocale(context, applied)
        committed = applied.rawLanguageTag
    }

    val listState = rememberLazyListState()

    Box(Modifier.verticalFadingEdge(listState)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 8.dp,
                end = 8.dp,
                top = 8.dp,
                bottom = 24.dp,
            ),
        ) {
            items(
                languages,
                key = { it.rawLanguageTag },
            ) { model ->
                RadioItem(
                    titleStr = model.localizedName,
                    subtitleStr = if (model.nativeName != model.localizedName) {
                        model.nativeName
                    } else {
                        null
                    },
                    selected = model.rawLanguageTag == committed,
                    onClick = { save(model.rawLanguageTag) },
                )
            }
        }
    }
}
