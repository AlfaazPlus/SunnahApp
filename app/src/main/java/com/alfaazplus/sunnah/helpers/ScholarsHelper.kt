package com.alfaazplus.sunnah.helpers

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object ScholarsHelper {
    fun getScholarRankName(rank: Int?): String? {
        return when (rank) {
            0 -> "Prophet"
            1 -> "Companion (RA)"
            2 -> "Follower (Tabi')"
            3 -> "Successor (Taba' Tabi')"
            4 -> "3rd Century AH"
            else -> null
        }
    }

    @Composable
    fun getScholarRankColor(rank: Int?): Color {
        return when (rank) {
            0 -> MaterialTheme.colorScheme.primary
            1 -> Color.Magenta
            2 -> Color.Cyan
            3 -> Color.Yellow
            4 -> Color.Blue
            else -> Color.Blue
        }
    }

    fun getInterestNames(interests: String?): String {
        val interestIds = interests
            ?.split(",")
            ?.map {
                it
                    .trim()
                    .toInt()
            } ?: return ""

        return interestIds
            .map { id ->
                when (id) {
                    1 -> "Tafsir/Quran"
                    2 -> "Recitation/Quran"
                    3 -> "Hadith"
                    4 -> "Narrator"
                    5 -> "Fiqh"
                    6 -> "Aqeedah"
                    7 -> "History"
                    8 -> "Seerah"
                    9 -> "Theology"
                    10 -> "Medicine"
                    11 -> "Science"
                    14 -> "Art/Poetry"
                    16 -> "Commander"
                    17 -> "Khalifah"
                    18 -> "Governor"
                    19 -> "Qadhi(Judge)"
                    20 -> "Reformer"
                    21 -> "Thinker"
                    22 -> "Linguistic"
                    else -> ""
                }
            }
            .filter { it.isNotEmpty() }
            .joinToString(", ")
    }
}