package com.alfaazplus.sunnah.ui.viewModels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.alfaazplus.sunnah.db.databases.AppDatabase
import com.alfaazplus.sunnah.db.databases.HadithDatabase
import com.alfaazplus.sunnah.helpers.DatabaseHelperV2
import com.alfaazplus.sunnah.helpers.HadithHelper
import com.alfaazplus.sunnah.ui.utils.preferences.AppPreferences
import com.alfaazplus.sunnah.ui.utils.shared_preference.SPHadithConfigs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val database: HadithDatabase,
    private val legacy: AppDatabase,
) : ViewModel() {
    var loading by mutableStateOf(false)

    suspend fun initializeHadiths(context: Context) {
        val currentVersion = HadithHelper.PREBUILT_HADITHS_VERSION
        val storedVersion = AppPreferences.getAssetHadithsImportedVersion()

        if (storedVersion >= currentVersion) {
            return
        }

        loading = true

        DatabaseHelperV2.populateHadithDataFromAssets(context, database, legacy) // todo: migrate user data

        loading = false
    }
}
