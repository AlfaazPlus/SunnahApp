package com.alfaazplus.sunnah.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.alfaazplus.sunnah.ui.MainApp
import com.alfaazplus.sunnah.ui.activities.base.BaseActivity
import com.alfaazplus.sunnah.ui.theme.SunnahAppTheme
import com.alfaazplus.sunnah.ui.utils.shared_preference.SPAppActions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    val intentFlow = MutableStateFlow<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SPAppActions.getRequireOnboarding(this)) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        intentFlow.value = intent

        setContent {
            SunnahAppTheme { MainApp(intentFlow) }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
        intentFlow.value = intent
    }
}
