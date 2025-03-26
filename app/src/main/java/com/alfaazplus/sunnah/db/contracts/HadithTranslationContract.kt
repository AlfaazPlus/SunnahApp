package com.alfaazplus.sunnah.db.contracts

object HadithTranslationContract {
    const val TABLE_NAME = "hadith_translation"

    object Columns {
        const val ID = "hadith_translation_id"
        const val COLLECTION_ID = "collection_id"
        const val URN = "urn"
        const val AR_URN = "ar_urn"
        const val HADITH_PREFIX = "hadith_prefix"
        const val HADITH_TEXT = "hadith_text"
        const val HADITH_SUFFIX = "hadith_suffix"
        const val COMMENTS = "comments"
        const val GRADES = "grades"
        const val GRADED_BY = "graded_by"
        const val REFERENCE = "reference"
        const val REF_IN_BOOK = "ref_in_book"
        const val REF_USC_MSA = "ref_usc_msa"
        const val REF_ENG = "ref_eng"
        const val LANG_CODE = "lang_code"
    }
}
