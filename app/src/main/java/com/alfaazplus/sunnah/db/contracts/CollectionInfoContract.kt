package com.alfaazplus.sunnah.db.contracts

object CollectionInfoContract {
    const val TABLE_NAME = "collection_info"

    object Columns {
        const val ID = "collection_info_id"
        const val COLLECTION_ID = "collection_id"
        const val NAME = "name"
        const val INTRO = "intro"
        const val DESCRIPTION = "description"
        const val NUMBERING_SOURCE = "numbering_source"
        const val LANGUAGE_CODE = "language_code"
    }
}
