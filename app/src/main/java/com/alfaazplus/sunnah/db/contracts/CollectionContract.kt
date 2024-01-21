package com.alfaazplus.sunnah.db.contracts

object CollectionContract {
    const val TABLE_NAME = "collection"

    object Columns {
        const val ID = "collection_id"
        const val TYPE = "type"
        const val HAS_VOLUMES = "has_volumes"
        const val HAS_BOOKS = "has_books"
        const val HAS_CHAPTERS = "has_chapters"
        const val NAME = "name"
        const val INTRO = "intro"
        const val DESCRIPTION = "description"
        const val NUMBERING_SOURCE = "numbering_source"
    }
}
