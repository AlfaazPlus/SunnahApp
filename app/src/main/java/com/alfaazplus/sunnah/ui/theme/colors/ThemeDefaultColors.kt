package com.alfaazplus.sunnah.ui.theme.colors

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

class ThemeDefaultColors : BaseColors() {
    override fun lightColors(): ColorScheme {
        return lightColorScheme(
            primary = Color(0xFF008B5B),
            onPrimary = Color(0xFFFFFFFF),

            primaryContainer = Color(0xFFB8F2D7),
            onPrimaryContainer = Color(0xFF002116),

            secondary = Color(0xFF0094A7),
            onSecondary = Color(0xFFFFFFFF),

            secondaryContainer = Color(0xFFB8F4FC),
            onSecondaryContainer = Color(0xFF001F24),

            tertiary = Color(0xFF9C3E00),
            onTertiary = Color(0xFFFFFFFF),

            tertiaryContainer = Color(0xFFFFDCCB),
            onTertiaryContainer = Color(0xFF321200),

            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),

            background = Color(0xFFF2F2F2),
            onBackground = Color(0xFF111111),

            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF1B1B1F),

            surfaceVariant = Color(0xFFE2E3E7),
            onSurfaceVariant = Color(0xFF44464F),

            surfaceContainerLow = Color(0xFFFFFFFF),
            surfaceContainer = Color(0xFFF5F5F5),

            inverseSurface = Color(0xFF303034),
            inverseOnSurface = Color(0xFFF2F0F4),

            inversePrimary = Color(0xFF5FE0AA),
        )
    }

    override fun darkColors(): ColorScheme {
        return darkColorScheme(

            primary = Color(0xFF008B5B),

            onPrimary = Color(0xFFFFFFFF),

            primaryContainer = Color(0xFF005C3D),
            onPrimaryContainer = Color(0xFFB8F2D7),

            secondary = Color(0xFF0094A7),
            onSecondary = Color(0xFFFFFFFF),

            secondaryContainer = Color(0xFF004E59),
            onSecondaryContainer = Color(0xFFB8F4FC),

            tertiary = Color(0xFF9C3E00),
            onTertiary = Color(0xFFFFFFFF),

            tertiaryContainer = Color(0xFF6D2C01),
            onTertiaryContainer = Color(0xFFFFDCCB),

            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),

            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),

            background = Color(0xFF101010),
            onBackground = Color(0xFFE5E5E5),

            surface = Color(0xFF1A1A1A),
            onSurface = Color(0xFFE5E5E5),

            surfaceVariant = Color(0xFF30343A),
            onSurfaceVariant = Color(0xFFC6C6CC),

            surfaceContainerLow = Color(0xFF161616),
            surfaceContainer = Color(0xFF242424),

            inverseSurface = Color(0xFFE5E5E5),
            inverseOnSurface = Color(0xFF1A1A1A),

            inversePrimary = Color(0xFF5FE0AA),
        )
    }
}
