package com.alfaazplus.sunnah.helpers

import com.alfaazplus.sunnah.repository.hadith.HADITH_COLLECTIONS
import com.alfaazplus.sunnah.ui.models.CollectionWithInfo

object HadithHelper {
    fun getIncludedCollections(): List<CollectionWithInfo> {
        return HADITH_COLLECTIONS.map {
            CollectionWithInfo(
                collection = DatabaseHelper.toHCollection(it.first),
                info = DatabaseHelper.toHCollectionInfo(it.second)
            )
        }
    }
}