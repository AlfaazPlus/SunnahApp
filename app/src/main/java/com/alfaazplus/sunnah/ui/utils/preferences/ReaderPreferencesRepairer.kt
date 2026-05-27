package com.alfaazplus.sunnah.ui.utils.preferences

import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils.DEFAULT_TRANSLATION
import com.alfaazplus.sunnah.ui.utils.reader.TranslationUtils.isBuiltInTranslation
import javax.inject.Inject

class ReaderPreferencesRepairer @Inject constructor(
    private val repository: HadithRepository,
) {
    suspend fun repairIfNeeded() {
        val selectedTranslation = ReaderPreferences.getHadithTranslation()
        if (isBuiltInTranslation(selectedTranslation)) return

        val isDownloaded = repository
            .getDownloadedTranslations(listOf(selectedTranslation))
            .isNotEmpty()

        if (!isDownloaded) {
            ReaderPreferences.setHadithTranslation(DEFAULT_TRANSLATION.langCode)
        }
    }
}
