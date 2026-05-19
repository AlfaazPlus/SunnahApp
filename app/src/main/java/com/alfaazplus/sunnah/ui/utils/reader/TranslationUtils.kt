package com.alfaazplus.sunnah.ui.utils.reader

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences.observeHadithTranslation
import kotlinx.coroutines.runBlocking


enum class HadithTranslation(val langCode: String, val labelRes: Int) {
    ENGLISH("en", R.string.lang_english),
    URDU("ur", R.string.lang_urdu),
    BENGALI("bn", R.string.lang_bengali),
    INDONESIAN("id", R.string.lang_indonesian),
    FRENCH("fr", R.string.lang_french);

    companion object {
        fun fromLangCode(langCode: String): HadithTranslation {
            return entries.find { it.langCode == langCode } ?: ENGLISH
        }
    }
}


object TranslationUtils {
    val DEFAULT_TRANSLATION = HadithTranslation.ENGLISH

    val AVAILABLE_TRANSLATIONS = HadithTranslation.entries

    fun isBuiltInTranslation(id: String): Boolean {
        return id == "en"
    }

    private fun getHadithTranslationLabelRes(langCode: String): Int {
        return HadithTranslation.fromLangCode(langCode).labelRes
    }

    fun getHadithTranslationLabel(context: Context): String {
        return runBlocking {
            context.getString(getHadithTranslationLabelRes(ReaderPreferences.getHadithTranslation()))
        }
    }

    @Composable
    fun resolveHadithTranslationLabel(): String {
        return stringResource(getHadithTranslationLabelRes(observeHadithTranslation()))
    }
}
