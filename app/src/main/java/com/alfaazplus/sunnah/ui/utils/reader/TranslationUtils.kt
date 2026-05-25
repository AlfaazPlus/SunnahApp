package com.alfaazplus.sunnah.ui.utils.reader

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import com.alfaazplus.sunnah.R
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences
import com.alfaazplus.sunnah.ui.utils.preferences.ReaderPreferences.observeHadithTranslation
import kotlinx.coroutines.runBlocking


enum class HadithTranslation(val langCode: String, val label: String, val isComingSoon: Boolean = false) {
    ENGLISH("en", "English"),
    URDU("ur", "اردو"),
    BENGALI("bn", "বাংলা", true),
    INDONESIAN("id", "Bahasa Indonesia", true),
    FRENCH("fr", "Française", true);

    companion object {
        fun fromLangCode(langCode: String): HadithTranslation {
            return entries.find { it.langCode == langCode } ?: ENGLISH
        }
    }
}


object TranslationUtils {
    const val ARABIC_LANG = "ar"
    const val ENGLISH_LANG = "en"

    val DEFAULT_TRANSLATION = HadithTranslation.ENGLISH

    val AVAILABLE_TRANSLATIONS = HadithTranslation.entries

    fun isBuiltInTranslation(id: String): Boolean {
        return id == ENGLISH_LANG
    }

    private fun getHadithTranslation(langCode: String): HadithTranslation {
        return HadithTranslation.fromLangCode(langCode)
    }

    fun getHadithTranslationLabel(): String {
        return runBlocking {
            getHadithTranslation(ReaderPreferences.getHadithTranslation()).label
        }
    }

    @Composable
    fun resolveHadithTranslationLabel(): String {
        return getHadithTranslation(observeHadithTranslation()).label
    }

    fun getNoTranslationMessage(context: Context, langCode: String): AnnotatedString {
        val message = when (langCode) {
            "bn" -> context.getString(R.string.noBanglaTranslation)
            "fr" -> context.getString(R.string.noFrenchTranslation)
            "in" -> context.getString(R.string.noIndonesianTranslation)
            "ur" -> context.getString(R.string.noUrduTranslation)
            else -> ""
        }

        return buildAnnotatedString {
            withStyle(
                SpanStyle(
                    fontStyle = FontStyle.Italic,
                )
            ) {
                append(message)
            }
        }
    }

    fun langCodeFromId(translationId: String): String {
        // Currently the id is itself the language code
        return translationId
    }

    fun contentLangCodes(preferredId: String): List<String> {
        return listOf(ARABIC_LANG, langCodeFromId(preferredId)).distinct()
    }

    fun metadataLangCodes(preferredLangCode: String): List<String> {
        return listOf(ARABIC_LANG, preferredLangCode).distinct()
    }

    fun gradeLangCodes(preferredLangCode: String): List<String> {
        return listOf(ENGLISH_LANG, langCodeFromId(preferredLangCode)).distinct()
    }
}
