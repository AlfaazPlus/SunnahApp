package com.alfaazplus.sunnah.ui.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.alfaazplus.sunnah.db.databases.HadithDatabase
import com.alfaazplus.sunnah.db.databases.HadithDatabaseLegacy
import com.alfaazplus.sunnah.db.databases.UserDatabase
import com.alfaazplus.sunnah.db.databases.UserDatabaseLegacy
import com.alfaazplus.sunnah.helpers.DatabaseHelper
import com.alfaazplus.sunnah.helpers.HadithHelper
import com.alfaazplus.sunnah.helpers.UserDataMigrator
import com.alfaazplus.sunnah.ui.utils.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class HadithSetupViewModel @Inject constructor(
    private val database: HadithDatabase,
    private val legacy: HadithDatabaseLegacy,
    private val legacyUserDatabase: UserDatabaseLegacy,
    private val userDatabaseV2: UserDatabase,
) : ViewModel() {
    private val _isSetUp = MutableStateFlow(false)
    private val _isSettingUp = MutableStateFlow(false)
    val isSettingUp = _isSettingUp.asStateFlow()

    private val initMutex = Mutex()

    suspend fun initializeHadiths(context: Context) {
        initMutex.withLock {
            if (_isSetUp.value) {
                return
            }

            val currentVersion = HadithHelper.PREBUILT_HADITHS_VERSION
            val storedVersion = AppPreferences.getAssetHadithsImportedVersion()
            val userDataMigrated = AppPreferences.getUserDataV2MigratedVersion()

            val needsHadithImport = storedVersion != currentVersion
            val needsUserDataMigration = !userDataMigrated

            if (!needsHadithImport && !needsUserDataMigration) {
                _isSetUp.value = true
                return
            }

            _isSettingUp.value = true

            try {
                if (needsHadithImport) {
                    DatabaseHelper.populateHadithDataFromAssets(context, database)
                }

                if (needsUserDataMigration) {
                    UserDataMigrator.migrate(
                        legacyUserDao = legacyUserDatabase.dao,
                        legacyHadithDao = legacy.hadithDao,
                        hadithDaoV2 = database.hadithDao,
                        userDatabaseV2 = userDatabaseV2,
                    )

                    AppPreferences.setUserDataV2MigratedVersion(true)
                    // DatabaseHelper.cleanupLegacyHadithData(legacy)
                }
            } finally {
                _isSetUp.value = true
                _isSettingUp.value = false
            }
        }
    }
}
