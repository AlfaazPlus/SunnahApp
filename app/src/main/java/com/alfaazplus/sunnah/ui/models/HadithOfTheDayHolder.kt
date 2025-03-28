package com.alfaazplus.sunnah.ui.models

import java.util.Date

data class HadithOfTheDayHolder(
    val urn: String,
    val createdAt: Date,
) {
    override fun toString(): String {
        return "$urn@${createdAt.time}"
    }

    companion object {
        fun parse(value: String): HadithOfTheDayHolder? {
            val parts = value.split("@")
            if (parts.size != 2) {
                return null
            }

            try {
                val hotd = HadithOfTheDayHolder(
                    urn = parts[0], createdAt = Date(parts[1].toLong())
                )

                // check if needs refresh
                val currentTime = System.currentTimeMillis()
                val diff = currentTime - hotd.createdAt.time
                val diffInHours = diff / (1000 * 60 * 60)
                if (diffInHours >= 24) {
                    return null
                }

                return hotd
            } catch (e: Exception) {
                return null
            }
        }
    }
}