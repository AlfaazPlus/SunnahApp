package com.alfaazplus.sunnah.db.entities.hadith

import androidx.room.Embedded
import androidx.room.Relation
import com.alfaazplus.sunnah.db.contracts.ChapterContract
import com.alfaazplus.sunnah.db.contracts.ChapterInfoContract
import com.alfaazplus.sunnah.db.entities.hadith.entities.HChapter
import com.alfaazplus.sunnah.db.entities.hadith.entities.HChapterInfo

@Deprecated("v2")
data class HadithChapter(
    @Embedded val chapter: HChapter,
    @Relation(
        parentColumn = ChapterContract.Columns.ID,
        entityColumn = ChapterInfoContract.Columns.CHAPTER_ID
    )
    val info: HChapterInfo
)
