package com.alfaazplus.sunnah.ui.models.userdata

import com.alfaazplus.sunnah.db.entities.userdata.v2.ReadHistory
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserBookmark
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserCollectionItem
import com.alfaazplus.sunnah.db.relations.HadithWithContents

data class UserDataUserItem(
    val hwc: HadithWithContents?,
    val visibleNumbering: String,
    val bookTitle: String,
    var translationText: String?,
) {
    override fun toString(): String {
        return "visibleNumbering=${visibleNumbering}, bookTitle=${bookTitle}"
    }
}

data class ReadHistoryNormalized(
    val item: ReadHistory,
    val ui: UserDataUserItem,
)

data class UserCollectionItemNormalized(
    val item: UserCollectionItem,
    val ui: UserDataUserItem,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserCollectionItemNormalized) return false
        return item.hadithId == other.item.hadithId
    }

    override fun hashCode(): Int = item.hadithId.hashCode()
}

data class UserBookmarkNormalized(
    val item: UserBookmark,
    val ui: UserDataUserItem,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserBookmarkNormalized) return false
        return item.hadithId == other.item.hadithId
    }

    override fun hashCode(): Int = item.hadithId.hashCode()
}
