package com.alfaazplus.sunnah.ui.models

import com.alfaazplus.sunnah.db.entities.hadith.entities.HCollection
import com.alfaazplus.sunnah.db.entities.hadith.entities.HCollectionInfo

@Deprecated("v2")
data class CollectionWithInfo(
    val collection: HCollection,
    val info: HCollectionInfo?,
    var isDownloaded: Boolean? = null,
)
