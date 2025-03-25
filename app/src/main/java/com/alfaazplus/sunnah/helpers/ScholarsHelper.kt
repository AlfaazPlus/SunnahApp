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
}