package com.alfaazplus.sunnah.ui.utils.shared_preference

import android.content.Context
import com.alfaazplus.sunnah.ui.utils.keys.Keys

object SPAppConfigs {
    const val LOCALE_DEFAULT = "default"

    private fun sp(context: Context) = context.getSharedPreferences("sp_app_configs", Context.MODE_PRIVATE)

    fun getLocale(context: Context): String {
        return sp(context).getString(Keys.LOCALE, LOCALE_DEFAULT)!!
    }

    fun setLocale(context: Context, locale: String) {
        sp(context).edit().putString(Keys.LOCALE, locale).apply()
    }
}