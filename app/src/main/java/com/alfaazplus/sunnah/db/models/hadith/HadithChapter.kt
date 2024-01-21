package com.alfaazplus.sunnah.db.models.hadith

import androidx.room.Embedded
import androidx.room.Relation
import com.alfaazplus.sunnah.db.contracts.ChapterContract
import com.alfaazplus.sunnah.db.contracts.ChapterInfoContract
import com.alfaazplus.sunnah.db.models.hadith.entities.HChapter
import com.alfaazplus.sunnah.db.models.hadith.entities.HChapterInfo

data class HadithChapter(
    @Embedded val chapter: HChapter,
    @Relation(
        parentColumn = ChapterContract.Columns.ID,
        entityColumn = ChapterInfoContract.Columns.CHAPTER_ID
    )
    val info: HChapterInfo
)
