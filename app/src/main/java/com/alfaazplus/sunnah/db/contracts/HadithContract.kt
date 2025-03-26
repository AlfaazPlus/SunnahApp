package com.alfaazplus.sunnah.db.contracts

object HadithContract {
    const val TABLE_NAME = "hadith"

    object Columns {
        const val ID = "hadith_id"
        const val URN = "urn"
        const val COLLECTION_ID = "collection_id"
        const val BOOK_ID = "book_id"
        const val CHAPTER_ID = "chapter_id"
        const val HADITH_NUMBER = "hadith_number"
        const val ORDER_IN_BOOK = "order_in_book"
        const val HADITH_PREFIX = "hadith_prefix"
        const val HADITH_TEXT = "hadith_text"
        const val HADITH_SUFFIX = "hadith_suffix"
        const val COMMENTS = "comments"
        const val GRADES = "grades"
        const val GRADED_BY = "graded_by"
        const val NARRATORS = "narrators"
        const val NARRATORS2 = "narrators2"
        const val RELATED = "related"
    }
}
