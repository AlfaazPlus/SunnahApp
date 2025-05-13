package com.alfaazplus.sunnah.db.contracts.userdata

object UserCollectionItemContract {
    const val TABLE_NAME = "user_collection_item"

    object Columns {
        const val ID = "id"
        const val USER_COLLECTION_ID = "u_collection_id"
        const val HADITH_COLLECTION_ID = "h_collection_id"
        const val HADITH_BOOK_ID = "h_book_id"
        const val HADITH_NUMBER = "hadith_number"
        const val REMARK = "remark"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
    }
}
