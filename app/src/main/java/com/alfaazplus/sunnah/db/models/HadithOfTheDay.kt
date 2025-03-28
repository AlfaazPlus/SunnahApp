package com.alfaazplus.sunnah.db.models

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.alfaazplus.sunnah.db.contracts.HadithContract
import com.alfaazplus.sunnah.db.contracts.HadithTranslationContract
import com.alfaazplus.sunnah.db.models.hadith.entities.Hadith
import com.alfaazplus.sunnah.db.models.hadith.entities.HadithTranslation

data class HadithOfTheDay(
    @Embedded val hadith: Hadith,
    @Relation(
        parentColumn = HadithContract.Columns.URN,
        entityColumn = HadithTranslationContract.Columns.AR_URN,
    ) val translation: HadithTranslation,
) {
    @Ignore
    var collectionName: String = ""
}
