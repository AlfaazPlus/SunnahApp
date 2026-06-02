package com.alfaazplus.sunnah.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.alfaazplus.sunnah.ui.activities.base.BaseActivity
import com.alfaazplus.sunnah.ui.screens.onboarding.OnboardingScreen
import com.alfaazplus.sunnah.ui.theme.SunnahAppTheme
import com.alfaazplus.sunnah.ui.utils.shared_preference.SPAppActions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SunnahAppTheme {
                OnboardingScreen(onComplete = ::complete)
            }
        }
    }

    private fun complete(pendingTranslation: String?) {
        SPAppActions.setRequireOnboarding(this, false)
        SPAppActions.setPendingHadithTranslation(this, pendingTranslation)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
