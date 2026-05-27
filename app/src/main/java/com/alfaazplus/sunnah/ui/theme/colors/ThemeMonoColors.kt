package com.alfaazplus.sunnah.ui.theme.colors

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

class ThemeMonoColors : BaseColors() {
    override fun lightColors(): ColorScheme {
        return lightColorScheme(

            primary = Color(0xFF616161),
            onPrimary = Color(0xFFFFFFFF),

            primaryContainer = Color(0xFFDCDCDC),
            onPrimaryContainer = Color(0xFF272727),

            secondary = Color(0xFF797979),
            onSecondary = Color(0xFFFFFFFF),

            secondaryContainer = Color(0xFFE5E5E5),
            onSecondaryContainer = Color(0xFF272727),

            tertiary = Color(0xFF8C8C8C),
            onTertiary = Color(0xFFFFFFFF),

            tertiaryContainer = Color(0xFFF0F0F0),
            onTertiaryContainer = Color(0xFF272727),

            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),

            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),

            background = Color(0xFFF2F2F2),
            onBackground = Color(0xFF272727),

            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF272727),

            surfaceVariant = Color(0xFFE0E0E0),
            onSurfaceVariant = Color(0xFF3D3D3D),

            surfaceContainerLow = Color(0xFFFFFFFF),
            surfaceContainer = Color(0xFFFFFFFF),

            inverseOnSurface = Color(0xFFF5F5F5),
            inverseSurface = Color(0xFF303030),

            inversePrimary = Color(0xFFC1C1C1),
        )
    }

    override fun darkColors(): ColorScheme {
        return darkColorScheme(

            // AMOLED-OPTIMIZED MONO
            primary = Color(0xFFBDBDBD),
            onPrimary = Color(0xFF000000),

            primaryContainer = Color(0xFF2A2A2A),
            onPrimaryContainer = Color(0xFFE5E5E5),

            secondary = Color(0xFFA8A8A8),
            onSecondary = Color(0xFF000000),

            secondaryContainer = Color(0xFF1F1F1F),
            onSecondaryContainer = Color(0xFFDADADA),

            tertiary = Color(0xFFD0D0D0),
            onTertiary = Color(0xFF000000),

            tertiaryContainer = Color(0xFF2F2F2F),
            onTertiaryContainer = Color(0xFFF0F0F0),

            error = Color(0xFFFFB4AB),
            onError = Color(0xFF000000),

            errorContainer = Color(0xFF5C0006),
            onErrorContainer = Color(0xFFFFDAD6),

            // TRUE AMOLED BLACK
            background = Color(0xFF000000),
            onBackground = Color(0xFFE0E0E0),

            surface = Color(0xFF000000),
            onSurface = Color(0xFFE0E0E0),

            // Subtle layered grays
            surfaceVariant = Color(0xFF1A1A1A),
            onSurfaceVariant = Color(0xFFC5C5C5),

            // Elevated cards/dialogs
            surfaceContainerLow = Color(0xFF0A0A0A),
            surfaceContainer = Color(0xFF141414),

            inverseOnSurface = Color(0xFF000000),
            inverseSurface = Color(0xFFE0E0E0),

            inversePrimary = Color(0xFF707070),
        )
    }
}
