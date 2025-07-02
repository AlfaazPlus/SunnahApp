package com.alfaazplus.sunnah.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.alfaazplus.sunnah.ui.MainApp
import com.alfaazplus.sunnah.ui.activities.base.BaseActivity
import com.alfaazplus.sunnah.ui.theme.SunnahAppTheme
import com.alfaazplus.sunnah.ui.utils.ThemeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    val intentFlow = MutableStateFlow<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intentFlow.value = intent

        setContent {
            val isDarkTheme = ThemeUtils.isDarkTheme()
            val colorScheme = ThemeUtils.getColorScheme(this, isDarkTheme)

            SunnahAppTheme(
                darkTheme = isDarkTheme,
                colorScheme = colorScheme,
            ) { MainApp(intentFlow) }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
        intentFlow.value = intent
    }
}