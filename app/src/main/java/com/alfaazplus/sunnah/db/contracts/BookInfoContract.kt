package com.alfaazplus.sunnah.db.contracts

object BookInfoContract {
    const val TABLE_NAME = "book_info"

    object Columns {
        const val ID = "book_info_id"
        const val BOOK_ID = "book_id"
        const val COLLECTION_ID = "collection_id"
        const val TITLE = "title"
        const val INTRO = "intro"
        const val DESCRIPTION = "description"
        const val LANGUAGE_CODE = "language_code"
    }
}
