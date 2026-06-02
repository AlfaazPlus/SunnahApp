package com.alfaazplus.sunnah.ui.utils.shared_preference

import android.content.Context
import androidx.core.content.edit
import com.alfaazplus.sunnah.ui.utils.preferences.AppPreferences
import kotlinx.coroutines.runBlocking

object SPAppActions {
    private const val SP_APP_ACTION = "sp_app_action"
    private const val KEY_ONBOARDING_REQUIRED = "app.action.onboarding_required"
    private const val KEY_PENDING_HADITH_TRANSLATION = "app.action.pending_hadith_translation"

    private fun sp(ctx: Context) = ctx.getSharedPreferences(SP_APP_ACTION, Context.MODE_PRIVATE)

    fun getRequireOnboarding(ctx: Context): Boolean {
        val preferences = sp(ctx)
        return preferences.getBoolean(KEY_ONBOARDING_REQUIRED, true)
    }

    fun setRequireOnboarding(ctx: Context, require: Boolean) {
        sp(ctx).edit { putBoolean(KEY_ONBOARDING_REQUIRED, require) }
    }

    fun setPendingHadithTranslation(ctx: Context, langCode: String?) {
        sp(ctx).edit {
            if (langCode.isNullOrBlank()) {
                remove(KEY_PENDING_HADITH_TRANSLATION)
            } else {
                putString(KEY_PENDING_HADITH_TRANSLATION, langCode)
            }
        }
    }

    fun getPendingHadithTranslation(ctx: Context): String? {
        return sp(ctx).getString(KEY_PENDING_HADITH_TRANSLATION, null)
    }

    fun clearPendingHadithTranslation(ctx: Context) {
        sp(ctx).edit { remove(KEY_PENDING_HADITH_TRANSLATION) }
    }
}
