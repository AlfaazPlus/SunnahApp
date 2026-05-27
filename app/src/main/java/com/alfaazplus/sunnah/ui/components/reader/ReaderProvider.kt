package com.alfaazplus.sunnah.ui.components.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.components.common.Loader
import com.alfaazplus.sunnah.ui.utils.ThemeUtils
import com.alfaazplus.sunnah.ui.viewModels.HadithSetupViewModel


data class ReaderData(
    val containerColor: Color,
    val contentColor: Color,
)

val LocalReader = staticCompositionLocalOf<ReaderData> {
    error("LocalReader not provided")
}

@Composable
fun ReaderProvider(
    setupVm: HadithSetupViewModel = hiltViewModel(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val isDark = ThemeUtils.observeDarkTheme()
    val isSettingUp by setupVm.isSettingUp.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        setupVm.initializeHadiths(context)
    }

    if (isSettingUp) {
        SettingUpOverlay()
        return
    }

    CompositionLocalProvider(
        LocalReader provides ReaderData(
            containerColor = if (isDark) colorScheme.background
            else colorScheme.surface,
            contentColor = if (isDark) colorScheme.onBackground
            else colorScheme.onSurface,
        )
    ) {
        ActionsProvider {
            content()
        }
    }
}

@Composable
fun SettingUpOverlay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Loader()
            Text(stringResource(R.string.setting_up))
        }
    }
}
