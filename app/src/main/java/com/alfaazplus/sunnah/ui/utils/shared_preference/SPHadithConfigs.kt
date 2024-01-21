package com.alfaazplus.sunnah.ui.utils.shared_preference

import android.content.Context
import com.alfaazplus.sunnah.ui.utils.keys.Keys

object SPHadithConfigs {
    private fun sp(context: Context) = context.getSharedPreferences("sp_hadith_configs", Context.MODE_PRIVATE)

    fun getAssetHadithsImported(context: Context): Boolean {
        return sp(context).getBoolean(Keys.ASSET_HADITHS_IMPORTED, false)
    }

    fun setAssetHadithsImported(context: Context, imported: Boolean) {
        sp(context).edit().putBoolean(Keys.ASSET_HADITHS_IMPORTED, imported).apply()
    }
}