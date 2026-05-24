package com.alfaazplus.sunnah.ui.theme.colors

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

class ThemePurpleColors : BaseColors() {
    override fun lightColors(): ColorScheme {
        return lightColorScheme(
            primary = Color(0xFFD14BA5),
            onPrimary = Color(0xFFFFFFFF),

            primaryContainer = Color(0xFFFFD9F2),
            onPrimaryContainer = Color(0xFF3B002B),

            secondary = Color(0xFFB05C9D),
            onSecondary = Color(0xFFFFFFFF),

            secondaryContainer = Color(0xFFFFDDF4),
            onSecondaryContainer = Color(0xFF35112E),

            tertiary = Color(0xFFFF8A80),
            onTertiary = Color(0xFFFFFFFF),

            tertiaryContainer = Color(0xFFFFDAD6),
            onTertiaryContainer = Color(0xFF3B0907),

            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),

            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),

            background = Color(0xFFFFF5FA),
            onBackground = Color(0xFF251821),

            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF251821),

            surfaceVariant = Color(0xFFF4DDEB),
            onSurfaceVariant = Color(0xFF5A4A54),

            surfaceContainerLow = Color(0xFFFFFFFF),
            surfaceContainer = Color(0xFFFFF0F7),

            inverseOnSurface = Color(0xFFFFF1F6),
            inverseSurface = Color(0xFF382B34),

            inversePrimary = Color(0xFFFFA7DF),
        )
    }

    override fun darkColors(): ColorScheme {
        return darkColorScheme(

            primary = Color(0xFFFFA7DF),
            onPrimary = Color(0xFF5A003F),

            primaryContainer = Color(0xFF7A1E63),
            onPrimaryContainer = Color(0xFFFFD9F2),

            secondary = Color(0xFFFFB7E8),
            onSecondary = Color(0xFF4A1F40),

            secondaryContainer = Color(0xFF633550),
            onSecondaryContainer = Color(0xFFFFDDF4),

            tertiary = Color(0xFFFFB4AB),
            onTertiary = Color(0xFF561E19),

            tertiaryContainer = Color(0xFF73342D),
            onTertiaryContainer = Color(0xFFFFDAD6),

            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),

            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),

            background = Color(0xFF140D12),
            onBackground = Color(0xFFF2E0EA),

            surface = Color(0xFF21171D),
            onSurface = Color(0xFFF2E0EA),

            surfaceVariant = Color(0xFF51424C),
            onSurfaceVariant = Color(0xFFD8C2CF),

            surfaceContainerLow = Color(0xFF1A1117),
            surfaceContainer = Color(0xFF2A1D25),

            inverseOnSurface = Color(0xFF21171D),
            inverseSurface = Color(0xFFF2E0EA),

            inversePrimary = Color(0xFFD14BA5),
        )
    }
}
