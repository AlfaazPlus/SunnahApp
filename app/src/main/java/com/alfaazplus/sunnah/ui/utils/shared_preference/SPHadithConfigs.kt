package com.alfaazplus.sunnah.ui.utils.shared_preference

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.alfaazplus.sunnah.ui.utils.keys.Keys

object SPHadithConfigs {
    fun getAssetHadithsImported(): Boolean {
        return DataStoreManager.read(booleanPreferencesKey(Keys.ASSET_HADITHS_IMPORTED), false)
    }

    suspend fun setAssetHadithsImported(imported: Boolean) {
        DataStoreManager.write(booleanPreferencesKey(Keys.ASSET_HADITHS_IMPORTED), imported)
    }
}