package com.alfaazplus.sunnah.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.alfaazplus.sunnah.db.interfaces.HadithMethods
import com.alfaazplus.sunnah.db.entities.v2.HadithContentEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithGradeEntity

data class HadithWithContents(
    @Embedded
    val hadith: HadithEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "hadith_id",
    )
    val contents: List<HadithContentEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "hadith_id",
    )
    val grades: List<HadithGradeEntity>,
) : HadithMethods by hadith
