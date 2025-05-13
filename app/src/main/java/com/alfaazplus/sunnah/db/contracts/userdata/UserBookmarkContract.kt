package com.alfaazplus.sunnah.db.contracts.userdata

object UserBookmarkContract {
    const val TABLE_NAME = "user_bookmark"

    object Columns {
        const val ID = "id"
        const val HADITH_COLLECTION_ID = "h_collection_id"
        const val HADITH_BOOK_ID = "h_book_id"
        const val HADITH_NUMBER = "hadith_number"
        const val REMARK = "remark"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
    }
}
