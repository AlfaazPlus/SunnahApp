package com.alfaazplus.sunnah.db.contracts

object BookContract {
    const val TABLE_NAME = "book"

    object Columns {
        const val ID = "book_id"
        const val COLLECTION_ID = "collection_id"
        const val SERIAL_NUMBER = "serial_number"
        const val ORDER_IN_COLLECTION = "order_in_collection"
        const val HADITH_START = "hadith_start"
        const val HADITH_END = "hadith_end"
        const val HADITH_COUNT = "hadith_count"
        const val TITLE = "title"
        const val INTRO = "intro"
        const val DESCRIPTION = "description"
    }
}
