package com.alfaazplus.sunnah.ui.utils

import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager

object AppUtils {

    @Composable
    fun observeDailyReminderEnabled(): Int {
        val enabled = DataStoreManager.observe(booleanPreferencesKey(Keys.DAILY_REMINDER), false)

        return when (enabled) {
            true -> R.string.enabled
            false -> R.string.disabled
        }
    }
}
