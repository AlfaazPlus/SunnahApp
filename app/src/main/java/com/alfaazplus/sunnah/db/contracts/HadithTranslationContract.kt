package com.alfaazplus.sunnah.db.contracts

object HadithTranslationContract {
    const val TABLE_NAME = "hadith_translation"

    object Columns {
        const val ID = "hadith_translation_id"
        const val COLLECTION_ID = "collection_id"
        const val URN = "urn"
        const val AR_URN = "ar_urn"
        const val NARRATOR_PREFIX = "narrator_prefix"
        const val HADITH_TEXT = "hadith_text"
        const val NARRATOR_SUFFIX = "narrator_suffix"
        const val COMMENTS = "comments"
        const val GRADES = "grades"
        const val REFERENCE = "reference"
        const val REFERENCE_IN_BOOK = "reference_in_book"
        const val REFERENCE_USC_MSA = "reference_usc_msa"
        const val REFERENCE_ENG = "reference_eng"
        const val LANGUAGE_CODE = "language_code"
    }
}
