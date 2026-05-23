package com.alfaazplus.sunnah.ui.models.userdata

import androidx.compose.ui.text.AnnotatedString
import com.alfaazplus.sunnah.db.entities.userdata.v2.ReadHistory
import com.alfaazplus.sunnah.db.relations.HadithWithContents

data class ReadHistoryNormalized(
    val item: ReadHistory,
    val hadith: HadithWithContents?,
    val collectionName: String?,
    val displayNumber: String,
    var translationText: AnnotatedString?,
) {
    fun key(): String = item.hadithId

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReadHistoryNormalized) return false
        return item.hadithId == other.item.hadithId
    }

    override fun hashCode(): Int = item.hadithId.hashCode()
}
