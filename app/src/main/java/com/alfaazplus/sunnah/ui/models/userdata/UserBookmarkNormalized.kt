package com.alfaazplus.sunnah.ui.models.userdata

import androidx.compose.ui.text.AnnotatedString
import com.alfaazplus.sunnah.db.models.hadith.entities.HadithTranslation
import com.alfaazplus.sunnah.db.models.userdata.UserBookmark


data class UserBookmarkNormalized(
    val item: UserBookmark,
    val translation: HadithTranslation,
    val collectionName: String,
    var translationText: AnnotatedString,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserCollectionItemNormalized) return false

        if (item.hadithCollectionId != other.item.hadithCollectionId) return false
        if (item.hadithBookId != other.item.hadithBookId) return false
        if (item.hadithNumber != other.item.hadithNumber) return false
        if (translation.arUrn != other.translation.arUrn) return false

        return true
    }

    override fun hashCode(): Int {
        var result = item.hashCode()
        result = 31 * result + translation.hashCode()
        result = 31 * result + collectionName.hashCode()
        result = 31 * result + translationText.hashCode()
        return result
    }
}