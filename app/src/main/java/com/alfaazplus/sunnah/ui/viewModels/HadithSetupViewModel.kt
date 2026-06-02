package com.alfaazplus.sunnah.ui.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.db.databases.HadithDatabase
import com.alfaazplus.sunnah.db.databases.HadithDatabaseLegacy
import com.alfaazplus.sunnah.db.databases.UserDatabase
import com.alfaazplus.sunnah.db.databases.UserDatabaseLegacy
import com.alfaazplus.sunnah.helpers.DatabaseHelper
import com.alfaazplus.sunnah.helpers.HadithHelper
import com.alfaazplus.sunnah.helpers.UserDataMigrator
import com.alfaazplus.sunnah.ui.search.SearchIndexScheduler
import com.alfaazplus.sunnah.ui.utils.managers.ResourceDownloadStatus
import com.alfaazplus.sunnah.ui.utils.managers.TranslationDownloadManager
import com.alfaazplus.sunnah.ui.utils.preferences.AppPreferences
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils
import com.alfaazplus.sunnah.ui.utils.shared_preference.SPAppActions
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

    private val _setupOverlay = MutableStateFlow<SetupOverlayState?>(null)
    val setupOverlay = _setupOverlay.asStateFlow()

    private val initMutex = Mutex()
    private var pendingTranslationApplied = false

    suspend fun initializeHadiths(context: Context) {
        initMutex.withLock {
            if (_isSetUp.value && pendingTranslationApplied) {
                return
            }

            val currentVersion = HadithHelper.PREBUILT_HADITHS_VERSION
            val storedVersion = AppPreferences.getAssetHadithsImportedVersion()
            val userDataMigrated = AppPreferences.getUserDataV2MigratedVersion()

            Logger.d("sunnahapp.db, current:$currentVersion, stored:$storedVersion")

            val needsHadithImport = storedVersion != currentVersion
            val needsUserDataMigration = !userDataMigrated

            if (needsHadithImport || needsUserDataMigration) {
                updateSetupProgress(0)
                _isSettingUp.value = true

                try {
                    if (needsHadithImport) {
                        DatabaseHelper.populateHadithDataFromAssets(
                            context = context,
                            database = database,
                            onProgress = ::updateSetupProgress,
                        )
                    }

                    if (needsUserDataMigration) {
                        UserDataMigrator.migrate(
                            legacyUserDao = legacyUserDatabase.dao,
                            legacyHadithDao = legacy.hadithDao,
                            hadithDaoV2 = database.hadithDao,
                            userDatabaseV2 = userDatabaseV2,
                        )

                        AppPreferences.setUserDataV2MigratedVersion(true)
                    }
                } finally {
                    _isSettingUp.value = false
                    _setupOverlay.value = null
                }
            }

            if (!pendingTranslationApplied) {
                applyPendingOnboardingTranslation(context)
                pendingTranslationApplied = true
            }

            _isSetUp.value = true
        }
    }

    private suspend fun applyPendingOnboardingTranslation(context: Context) {
        val pendingId = SPAppActions.getPendingHadithTranslation(context)
        SPAppActions.clearPendingHadithTranslation(context)

        if (pendingId.isNullOrBlank()) {
            return
        }

        val isValid = TranslationUtils.AVAILABLE_TRANSLATIONS.any {
            it.langCode == pendingId && !it.isComingSoon
        }
        if (!isValid) {
            return
        }

        if (TranslationUtils.isBuiltInTranslation(pendingId)) {
            ReaderPreferences.setHadithTranslation(pendingId)
            return
        }

        _setupOverlay.value = SetupOverlayState(
            messageRes = R.string.downloadingTranslationProgress,
            progress = 0,
        )
        _isSettingUp.value = true

        try {
            val status = TranslationDownloadManager.awaitDownload(
                context = context,
                translationId = pendingId,
                onProgress = { progress ->
                    _setupOverlay.value = SetupOverlayState(
                        messageRes = R.string.downloadingTranslationProgress,
                        progress = progress,
                    )
                },
            )
            if (status is ResourceDownloadStatus.Completed) {
                ReaderPreferences.setHadithTranslation(pendingId)
                SearchIndexScheduler.enqueueLang(context, pendingId)
            } else {
                Logger.d("Onboarding translation download failed for $pendingId: $status")
                ReaderPreferences.setHadithTranslation(TranslationUtils.DEFAULT_TRANSLATION.langCode)
            }
        } finally {
            _isSettingUp.value = false
            _setupOverlay.value = null
        }
    }

    private fun updateSetupProgress(progress: Int) {
        _setupOverlay.value = SetupOverlayState(
            messageRes = R.string.setting_up,
            progress = progress.coerceIn(0, 100),
        )
    }
}
