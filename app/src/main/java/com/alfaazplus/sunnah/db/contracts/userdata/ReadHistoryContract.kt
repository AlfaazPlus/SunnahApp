package com.alfaazplus.sunnah.db.contracts.userdata

object ReadHistoryContract {
    const val TABLE_NAME = "read_history"

    object Columns {
        const val HADITH_COLLECTION_ID = "h_collection_id"
        const val HADITH_BOOK_ID = "h_book_id"
        const val HADITH_NUMBER = "hadith_number"
        const val CREATED_AT = "created_at"
    }
}
