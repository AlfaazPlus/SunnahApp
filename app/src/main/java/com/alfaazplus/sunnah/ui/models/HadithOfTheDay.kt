package com.alfaazplus.sunnah.ui.models

import com.alfaazplus.sunnah.db.relations.HadithWithContents

data class HadithOfTheDay(
    val hwc: HadithWithContents,
    var collectionName: String,
)
