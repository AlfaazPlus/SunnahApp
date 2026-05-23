package com.alfaazplus.sunnah.ui.models.userdata

import androidx.compose.ui.text.AnnotatedString
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserBookmark
import com.alfaazplus.sunnah.db.relations.HadithWithContents

data class UserBookmarkNormalized(
    val item: UserBookmark,
    val hadith: HadithWithContents?,
    val collectionName: String?,
    val displayNumber: String,
    var translationText: AnnotatedString?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserBookmarkNormalized) return false
        return item.hadithId == other.item.hadithId
    }

    override fun hashCode(): Int = item.hadithId.hashCode()
}
