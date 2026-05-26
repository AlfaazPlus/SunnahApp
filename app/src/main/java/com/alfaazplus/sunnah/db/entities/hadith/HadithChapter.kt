package com.alfaazplus.sunnah.db.entities.hadith

import androidx.room3.Embedded
import androidx.room3.Relation
import com.alfaazplus.sunnah.db.contracts.ChapterContract
import com.alfaazplus.sunnah.db.contracts.ChapterInfoContract
import com.alfaazplus.sunnah.db.entities.hadith.entities.HChapter
import com.alfaazplus.sunnah.db.entities.hadith.entities.HChapterInfo

@Deprecated("v2")
data class HadithChapter(
    @Embedded val chapter: HChapter,
    @Relation(
        parentColumns = [ChapterContract.Columns.ID],
        entityColumns = [ChapterInfoContract.Columns.CHAPTER_ID],
    )
    val info: HChapterInfo
)
