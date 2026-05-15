package com.alfaazplus.sunnah.ui.utils.preferences

import androidx.datastore.preferences.core.intPreferencesKey
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager

object AppPreferences {
    private const val ASSET_HADITHS_IMPORTED_VERSION = "asset_hadiths_imported_version"

    suspend fun getAssetHadithsImportedVersion(): Int {
        return DataStoreManager.readFirst(intPreferencesKey(ASSET_HADITHS_IMPORTED_VERSION), 0)
    }

    suspend fun setAssetHadithsImportedVersion(imported: Int) {
        DataStoreManager.write(intPreferencesKey(ASSET_HADITHS_IMPORTED_VERSION), imported)
    }
}
