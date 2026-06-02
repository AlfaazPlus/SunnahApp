package com.alfaazplus.sunnah.ui.screens.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.utils.extension.verticalFadingEdge
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils

@Composable
fun OnboardingTranslationsPage(
    selectedId: String,
    onSelected: (String) -> Unit,
) {
    val listState = rememberLazyListState()

    androidx.compose.foundation.layout.Box(
        Modifier.verticalFadingEdge(listState),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 8.dp,
                end = 8.dp,
                top = 8.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(
                TranslationUtils.AVAILABLE_TRANSLATIONS,
                key = { it.langCode },
            ) { translation ->
                TranslationRow(
                    id = translation.langCode,
                    title = translation.label,
                    isComingSoon = translation.isComingSoon,
                    isSelected = translation.langCode == selectedId,
                    onSelect = { onSelected(translation.langCode) },
                )
            }
        }
    }
}

@Composable
private fun TranslationRow(
    id: String,
    title: String,
    isComingSoon: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    val isEnabled = !isComingSoon

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isComingSoon) 0.6f else 1f)
            .clickable(isEnabled) { onSelect() }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { if (isEnabled) onSelect() },
            enabled = isEnabled,
            colors = RadioButtonDefaults.colors(
                selectedColor = colorScheme.primary,
            ),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) colorScheme.primary else colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (!isComingSoon && !TranslationUtils.isBuiltInTranslation(id)) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.onboardTranslationWillDownload),
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        if (isComingSoon) {
            Surface(
                shape = shapes.small,
                color = colorScheme.secondaryContainer,
            ) {
                Text(
                    text = stringResource(R.string.comingSoon),
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }
    }
}
