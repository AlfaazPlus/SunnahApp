package com.alfaazplus.sunnah.db.contracts

object ChapterInfoContract {
    const val TABLE_NAME = "chapter_info"

    object Columns {
        const val ID = "chapter_info_id"
        const val COLLECTION_ID = "collection_id"
        const val BOOK_ID = "book_id"
        const val CHAPTER_ID = "chapter_id"
        const val SERIAL_NUMBER = "serial_number"
        const val TITLE = "title"
        const val INTRO = "intro"
        const val DESCRIPTION = "description"
        const val ENDING = "ending"
        const val LANGUAGE_CODE = "language_code"
    }
}
