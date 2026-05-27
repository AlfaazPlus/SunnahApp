package com.alfaazplus.sunnah.ui.components.homepage

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.LocalNavHostController
import com.alfaazplus.sunnah.ui.safeNavigate
import com.alfaazplus.sunnah.ui.utils.app.ResourceUpdateManager
import com.alfaazplus.sunnah.ui.utils.keys.Routes
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.reader.TranslationManager
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils

@Composable
fun TranslationUpdateBanner() {
    val context = LocalContext.current
    val navController = LocalNavHostController.current
    val selectedTranslationId = ReaderPreferences.observeHadithTranslation()
    val updateState by ResourceUpdateManager.updateState.collectAsState()

    LaunchedEffect(Unit) {
        ResourceUpdateManager.checkAndPerformUpdates()
    }

    val showBanner = remember(selectedTranslationId, updateState) {
        isSelectedTranslationUpdateAvailable(context, selectedTranslationId)
    }

    if (!showBanner) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, top = 10.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface,
        ),
        border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.18f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedUpdateAppIcon()

            Text(
                text = stringResource(R.string.msgTranslationUpdateAvailable),
                modifier = Modifier.weight(1f),
                style = typography.bodyMedium,
                color = colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )

            FilledTonalButton(
                onClick = { navController.safeNavigate(Routes.SETTINGS_TRANSLATIONS) },
            ) {
                Text(text = stringResource(R.string.update))
            }
        }
    }
}

private fun isSelectedTranslationUpdateAvailable(
    context: Context,
    translationId: String,
): Boolean {
    if (TranslationUtils.isBuiltInTranslation(translationId)) return false

    val resourceVersions = ResourceUpdateManager.getLocalVersions() ?: return false

    return TranslationManager.isUpdateAvailable(context, translationId, resourceVersions)
}
