package com.alfaazplus.sunnah.helpers

import androidx.compose.ui.graphics.Color
import com.alfaazplus.sunnah.repository.hadith.HADITH_COLLECTIONS
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo


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
}