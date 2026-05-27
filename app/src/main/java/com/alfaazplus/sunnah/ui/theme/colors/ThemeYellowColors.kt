package com.alfaazplus.sunnah.ui.theme.colors

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

class ThemeYellowColors : BaseColors() {
    override fun lightColors(): ColorScheme {
        return lightColorScheme(
            primary = Color(0xFF7B5A3A),
            onPrimary = Color(0xFFFFFFFF),

            primaryContainer = Color(0xFFF0DEC9),
            onPrimaryContainer = Color(0xFF2E1D10),

            secondary = Color(0xFF8A6A50),
            onSecondary = Color(0xFFFFFFFF),

            secondaryContainer = Color(0xFFF5E4D3),
            onSecondaryContainer = Color(0xFF352216),

            tertiary = Color(0xFF6E7458),
            onTertiary = Color(0xFFFFFFFF),

            tertiaryContainer = Color(0xFFE3E8D7),
            onTertiaryContainer = Color(0xFF202515),

            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),

            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),

            background = Color(0xFFF6EBDD),
            onBackground = Color(0xFF2F241B),

            surface = Color(0xFFFFF8EF),
            onSurface = Color(0xFF2F241B),

            surfaceVariant = Color(0xFFE6D6C5),
            onSurfaceVariant = Color(0xFF5B4A3D),

            surfaceContainerLow = Color(0xFFFFFBF5),
            surfaceContainer = Color(0xFFF9F0E5),

            inverseOnSurface = Color(0xFFFFF7EE),
            inverseSurface = Color(0xFF3B2F27),

            inversePrimary = Color(0xFFD8B58E),
        )
    }

    override fun darkColors(): ColorScheme {
        return darkColorScheme(
            primary = Color(0xFFD8B58E),
            onPrimary = Color(0xFF422D19),

            primaryContainer = Color(0xFF5A412C),
            onPrimaryContainer = Color(0xFFF0DEC9),

            secondary = Color(0xFFE0C2A6),
            onSecondary = Color(0xFF493121),

            secondaryContainer = Color(0xFF644A38),
            onSecondaryContainer = Color(0xFFF5E4D3),

            tertiary = Color(0xFFC8D0BA),
            onTertiary = Color(0xFF313827),

            tertiaryContainer = Color(0xFF49503E),
            onTertiaryContainer = Color(0xFFE3E8D7),

            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),

            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),

            background = Color(0xFF1E1712),
            onBackground = Color(0xFFF0E2D3),

            surface = Color(0xFF2A211A),
            onSurface = Color(0xFFF0E2D3),

            surfaceVariant = Color(0xFF5B4A3D),
            onSurfaceVariant = Color(0xFFE0CEBC),

            surfaceContainerLow = Color(0xFF241C16),
            surfaceContainer = Color(0xFF342920),

            inverseOnSurface = Color(0xFF2A211A),
            inverseSurface = Color(0xFFF0E2D3),

            inversePrimary = Color(0xFF7B5A3A),
        )
    }
}
