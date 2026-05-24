package com.alfaazplus.sunnah.ui.theme.colors

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

class ThemeRedColors : BaseColors() {
    override fun lightColors(): ColorScheme {
        return lightColorScheme(

            primary = Color(0xFFB3261E),
            onPrimary = Color(0xFFFFFFFF),

            primaryContainer = Color(0xFFFFDAD4),
            onPrimaryContainer = Color(0xFF410001),

            secondary = Color(0xFF8C5A55),
            onSecondary = Color(0xFFFFFFFF),

            secondaryContainer = Color(0xFFFFDAD5),
            onSecondaryContainer = Color(0xFF3A1F1B),

            tertiary = Color(0xFF9C6A00),
            onTertiary = Color(0xFFFFFFFF),

            tertiaryContainer = Color(0xFFFFE08A),
            onTertiaryContainer = Color(0xFF2F2000),

            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),

            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),

            background = Color(0xFFFFF4F2),
            onBackground = Color(0xFF221919),

            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF221919),

            surfaceVariant = Color(0xFFF3DEDA),
            onSurfaceVariant = Color(0xFF5A4745),

            surfaceContainerLow = Color(0xFFFFFFFF),
            surfaceContainer = Color(0xFFFFF1EE),

            inverseOnSurface = Color(0xFFFFF1EF),
            inverseSurface = Color(0xFF382E2C),

            inversePrimary = Color(0xFFFFB4A8),
        )
    }

    override fun darkColors(): ColorScheme {
        return darkColorScheme(

            primary = Color(0xFFFFB4A8),
            onPrimary = Color(0xFF690003),

            primaryContainer = Color(0xFF8C1D18),
            onPrimaryContainer = Color(0xFFFFDAD4),

            secondary = Color(0xFFFFBDB5),
            onSecondary = Color(0xFF522723),

            secondaryContainer = Color(0xFF6B3C37),
            onSecondaryContainer = Color(0xFFFFDAD5),

            tertiary = Color(0xFFFFC94D),
            onTertiary = Color(0xFF3D2A00),

            tertiaryContainer = Color(0xFF5A4300),
            onTertiaryContainer = Color(0xFFFFE08A),

            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),

            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),

            background = Color(0xFF140F0E),
            onBackground = Color(0xFFF0E0DD),

            surface = Color(0xFF211917),
            onSurface = Color(0xFFF0E0DD),

            surfaceVariant = Color(0xFF5A4745),
            onSurfaceVariant = Color(0xFFDBC2BE),

            surfaceContainerLow = Color(0xFF1A1312),
            surfaceContainer = Color(0xFF2A201E),

            inverseOnSurface = Color(0xFF211917),
            inverseSurface = Color(0xFFF0E0DD),

            inversePrimary = Color(0xFFB3261E),
        )
    }
}
