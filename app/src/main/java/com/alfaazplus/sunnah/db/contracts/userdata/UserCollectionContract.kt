package com.alfaazplus.sunnah.db.contracts.userdata

object UserCollectionContract {
    const val TABLE_NAME = "user_collection"

    object Columns {
        const val ID = "id"
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val COLOR = "color"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
    }
}
