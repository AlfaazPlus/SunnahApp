package com.alfaazplus.sunnah.ui.models

import com.alfaazplus.sunnah.db.models.hadith.entities.HCollection
import com.alfaazplus.sunnah.db.models.hadith.entities.HCollectionInfo

data class CollectionWithInfo(
    val collection: HCollection,
    val info: HCollectionInfo?,
    var isDownloaded: Boolean? = null,
    var isDownloading: Boolean? = null
)