package com.alfaazplus.sunnah.helpers

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.alfaazplus.sunnah.R

object ScholarsHelper {
    @Composable
    fun getScholarRankName(rank: Int?): String? {
        return when (rank) {
            0 -> stringResource(R.string.scholar_rank_prophet)
            1 -> stringResource(R.string.scholar_rank_companion)
            2 -> stringResource(R.string.scholar_rank_follower)
            3 -> stringResource(R.string.scholar_rank_successor)
            4 -> stringResource(R.string.scholar_rank_3rd_century)
            5 -> stringResource(R.string.scholar_rank_4th_century)
            6 -> stringResource(R.string.scholar_rank_5th_century)
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
