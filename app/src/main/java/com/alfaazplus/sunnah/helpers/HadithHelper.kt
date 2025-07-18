package com.alfaazplus.sunnah.helpers

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.core.text.parseAsHtml
import androidx.datastore.preferences.core.stringPreferencesKey
import com.alfaazplus.sunnah.db.models.HadithOfTheDay
import com.alfaazplus.sunnah.db.models.hadith.entities.HadithTranslation
import com.alfaazplus.sunnah.repository.hadith.HADITH_COLLECTIONS
import com.alfaazplus.sunnah.repository.hadith.HadithRepository
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo
import com.alfaazplus.sunnah.ui.models.HadithOfTheDayHolder
import com.alfaazplus.sunnah.ui.utils.keys.Keys
import com.alfaazplus.sunnah.ui.utils.shared_preference.DataStoreManager
import java.util.Date


val GRADE_COLORS = mapOf(
    1 to Color(76, 175, 80), // Sahih
    2 to Color(157, 145, 43), // Hasan
    3 to Color(244, 67, 54), // Daif
    4 to Color(158, 11, 0), // Maudu
)

object HadithHelper {
    fun getIncludedCollections(): List<CollectionWithInfo> {
        return HADITH_COLLECTIONS.map {
            CollectionWithInfo(
                collection = DatabaseHelper.toHCollection(it.first), info = DatabaseHelper.toHCollectionInfo(it.second)
            )
        }
    }

    fun getHadithGradeText(grade: String?, gradedBy: String?): Pair<Int, String>? {
        if (grade.isNullOrEmpty()) {
            return null;
        }

        var gradeText = grade

        if (!gradedBy.isNullOrEmpty()) {
            gradeText += " (${gradedBy})"
        }

        val gradeLower = grade.lowercase()
        var gradeType = 0

        if (gradeLower.contains("sahih")) {
            gradeType = 1
        } else if (gradeLower.contains("hasan")) {
            gradeType = 2
        } else if (gradeLower.contains("da'if")) {
            gradeType = 3
        } else if (gradeLower.contains("maudu")) {
            gradeType = 4
        }

        return Pair(gradeType, gradeText)
    }

    fun getHadithGradeColor(gradeType: Int): Color {
        return GRADE_COLORS.getOrElse(gradeType) { Color(115, 103, 102) }
    }

    suspend fun getHadithOfTheDay(repo: HadithRepository): HadithOfTheDay? {
        val hotdValue = DataStoreManager.read(stringPreferencesKey(Keys.HADITH_OF_THE_DAY), "")
        val hotdHolder = HadithOfTheDayHolder.parse(hotdValue)

        if (hotdHolder != null) {
            val hotd = repo.getHotd(hotdHolder.urn)
            if (hotd != null) {
                return hotd
            }
        }

        while (true) {
            val hotd = repo.getNewHotd() ?: return null

            val newHotdHolder = HadithOfTheDayHolder(hotd.hadith.urn, Date(System.currentTimeMillis()))

            DataStoreManager.write(stringPreferencesKey(Keys.HADITH_OF_THE_DAY), newHotdHolder.toString())

            return hotd
        }
    }

    fun shareHadith(context: Context, translation: HadithTranslation, collectionName: String, hadithNumber: String) {
        val textToShare = buildString {
            if (!translation.narratorPrefix.isNullOrBlank()) {
                appendLine(translation.narratorPrefix.parseAsHtml())
                appendLine()
            }

            appendLine(translation.hadithText.parseAsHtml())
            appendLine()
            appendLine("— $collectionName: $hadithNumber")
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, textToShare)
        }

        context.startActivity(
            Intent.createChooser(intent, "Share via")
        )
    }
}