package com.alfaazplus.sunnah.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.alfaazplus.sunnah.db.entities.v2.HadithContentEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithEntity
import com.alfaazplus.sunnah.db.entities.v2.HadithGradeEntity
import com.alfaazplus.sunnah.db.interfaces.HadithMethods

data class HadithWithContents(
    @Embedded
    val hadith: HadithEntity,
    @Relation(
        entity = HadithContentEntity::class,
        parentColumn = "id",
        entityColumn = "hadith_id",
    )
    val contents: List<HadithContentEntity>,
    @Relation(
        entity = HadithGradeEntity::class,
        parentColumn = "id",
        entityColumn = "hadith_id",
    )
    val grades: List<HadithGradeEntity>,
) : HadithMethods {
    override val collectionId get() = hadith.collectionId
    override val bookId get() = hadith.bookId
    override val hadithId get() = hadith.id
}
