package com.alfaazplus.sunnah.ui.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.alfaazplus.sunnah.db.databases.AppDatabase
import com.alfaazplus.sunnah.db.databases.HadithDatabase
import com.alfaazplus.sunnah.helpers.DatabaseHelper
import com.alfaazplus.sunnah.helpers.HadithHelper
import com.alfaazplus.sunnah.ui.utils.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HadithSetupViewModel @Inject constructor(
    private val database: HadithDatabase,
    private val legacy: AppDatabase,
) : ViewModel() {
    val _isSettingUp = MutableStateFlow(false)
    val isSettingUp = _isSettingUp.asStateFlow()

    suspend fun initializeHadiths(context: Context) {
        val currentVersion = HadithHelper.PREBUILT_HADITHS_VERSION
        val storedVersion = AppPreferences.getAssetHadithsImportedVersion()

        if (storedVersion >= currentVersion) {
            return
        }

        _isSettingUp.value = true

        DatabaseHelper.populateHadithDataFromAssets(context, database, legacy)

        // todo: migrate user data

        _isSettingUp.value = false
    }
}
