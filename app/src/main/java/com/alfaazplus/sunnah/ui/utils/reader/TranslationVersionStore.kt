package com.alfaazplus.sunnah.ui.utils.reader

import android.content.Context
import androidx.core.content.edit

class TranslationVersionStore(
    context: Context
) {
    companion object {
        private const val KEY_ITEM_VERSION_PREFIX = "translations.item.version."
    }

    private val appContext = context.applicationContext

    private fun sp() = appContext.getSharedPreferences("sp_translation_versions", Context.MODE_PRIVATE)

    fun getItemVersion(id: String): Int = sp().getInt(KEY_ITEM_VERSION_PREFIX + id, 0)

    fun setItemVersion(id: String, version: Int) {
        sp().edit { putInt(KEY_ITEM_VERSION_PREFIX + id, version) }
    }
}
