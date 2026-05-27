package com.alfaazplus.sunnah.ui.utils.preferences

import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfaazplus.sunnah.api.ResourceDownloadProxy
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import com.alfaazplus.sunnah.ui.utils.shared_preference.PrefKey

object AppPreferences {
    private val KEY_ASSET_HADITHS_IMPORTED_VERSION = PrefKey(intPreferencesKey("asset_hadiths_imported_version"), 0)
    private val KEY_USER_DATA_MIGRATED = PrefKey(booleanPreferencesKey("user_data_v2_migrated"), false)
    private val KEY_DOWNLOAD_PROXY = PrefKey(stringPreferencesKey("resource_download_proxy"), ResourceDownloadProxy.DEFAULT.name)

    suspend fun getAssetHadithsImportedVersion(): Int {
        return DataStoreManager.readFirst(KEY_ASSET_HADITHS_IMPORTED_VERSION)
    }

    suspend fun setAssetHadithsImportedVersion(imported: Int) {
        DataStoreManager.write(KEY_ASSET_HADITHS_IMPORTED_VERSION, imported)
    }

    suspend fun getUserDataV2MigratedVersion(): Boolean {
        return DataStoreManager.readFirst(KEY_USER_DATA_MIGRATED)
    }

    suspend fun setUserDataV2MigratedVersion(migrated: Boolean) {
        DataStoreManager.write(KEY_USER_DATA_MIGRATED, migrated)
    }

    fun getResourceDownloadProxy(): ResourceDownloadProxy {
        return DataStoreManager
            .read(KEY_DOWNLOAD_PROXY)
            .let { ResourceDownloadProxy.fromValue(it) }
    }

    suspend fun setResourceDownloadProxy(src: ResourceDownloadProxy) {
        DataStoreManager.write(KEY_DOWNLOAD_PROXY, src.value)
    }

    @Composable
    fun observeResourceDownloadProxy(): ResourceDownloadProxy {
        return DataStoreManager
            .observe(KEY_DOWNLOAD_PROXY)
            .let { ResourceDownloadProxy.fromValue(it) }
    }
}
