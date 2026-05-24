package com.alfaazplus.sunnah.ui.theme.colors

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

class ThemeVioletColors : BaseColors() {
    override fun lightColors(): ColorScheme {
        return lightColorScheme(
            primary = Color(0xFF7C4DFF),
            onPrimary = Color(0xFFFFFFFF),

            primaryContainer = Color(0xFFEBDDFF),
            onPrimaryContainer = Color(0xFF2E0052),

            secondary = Color(0xFF8B5CF6),
            onSecondary = Color(0xFFFFFFFF),

            secondaryContainer = Color(0xFFF0E4FF),
            onSecondaryContainer = Color(0xFF32005A),

            tertiary = Color(0xFFA855F7),
            onTertiary = Color(0xFFFFFFFF),

            tertiaryContainer = Color(0xFFF5D9FF),
            onTertiaryContainer = Color(0xFF3B0048),

            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),

            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),

            background = Color(0xFFF7F2FF),
            onBackground = Color(0xFF24182F),

            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF24182F),

            surfaceVariant = Color(0xFFE8DFF0),
            onSurfaceVariant = Color(0xFF52485B),

            surfaceContainerLow = Color(0xFFFFFFFF),
            surfaceContainer = Color(0xFFF5EEFF),

            inverseOnSurface = Color(0xFFF8EEFF),
            inverseSurface = Color(0xFF35263F),

            inversePrimary = Color(0xFFD7BAFF),
        )
    }

    override fun darkColors(): ColorScheme {
        return darkColorScheme(

            primary = Color(0xFFD7BAFF),
            onPrimary = Color(0xFF4A187F),

            primaryContainer = Color(0xFF62359A),
            onPrimaryContainer = Color(0xFFEBDDFF),

            secondary = Color(0xFFE0C8FF),
            onSecondary = Color(0xFF51238A),

            secondaryContainer = Color(0xFF6B42A5),
            onSecondaryContainer = Color(0xFFF0E4FF),

            tertiary = Color(0xFFF0B5FF),
            onTertiary = Color(0xFF5A0068),

            tertiaryContainer = Color(0xFF7A2F8E),
            onTertiaryContainer = Color(0xFFF5D9FF),

            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),

            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),

            background = Color(0xFF140F1A),
            onBackground = Color(0xFFF0DFFF),

            surface = Color(0xFF201728),
            onSurface = Color(0xFFF0DFFF),

            surfaceVariant = Color(0xFF52485B),
            onSurfaceVariant = Color(0xFFD1C3DB),

            surfaceContainerLow = Color(0xFF1A1321),
            surfaceContainer = Color(0xFF2B2034),

            inverseOnSurface = Color(0xFF201728),
            inverseSurface = Color(0xFFF0DFFF),

            inversePrimary = Color(0xFF7C4DFF),
        )
    }
}
