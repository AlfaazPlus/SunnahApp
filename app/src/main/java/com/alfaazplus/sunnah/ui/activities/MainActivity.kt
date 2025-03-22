package com.alfaazplus.sunnah.ui.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import com.alfaazplus.sunnah.ui.MainApp
import com.alfaazplus.sunnah.ui.activities.base.BaseActivity
import com.alfaazplus.sunnah.ui.theme.SunnahAppTheme
import com.alfaazplus.sunnah.ui.utils.ThemeUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isDarkTheme = ThemeUtils.isDarkTheme()
            val colorScheme = ThemeUtils.getColorScheme(this, isDarkTheme)

            SunnahAppTheme(
                darkTheme = isDarkTheme,
                colorScheme = colorScheme,
            ) { MainApp() }
        }
    }
}