package com.alfaazplus.sunnah.db.dao

import androidx.room3.Dao
import androidx.room3.Query
import com.alfaazplus.sunnah.db.entities.userdata.ReadHistoryLegacy
import com.alfaazplus.sunnah.db.entities.userdata.UserBookmarkLegacy
import com.alfaazplus.sunnah.db.entities.userdata.UserCollectionLegacy
import com.alfaazplus.sunnah.db.entities.userdata.UserCollectionItemLegacy

@Deprecated("")
@Dao
interface UserDataDaoLegacy {
    @Query("SELECT * FROM read_history")
    suspend fun getAllReadHistory(): List<ReadHistoryLegacy>

    @Query("SELECT * FROM user_bookmark")
    suspend fun getAllUserBookmarks(): List<UserBookmarkLegacy>

    @Query("SELECT * FROM user_collection")
    suspend fun getAllUserCollections(): List<UserCollectionLegacy>

    @Query("SELECT * FROM user_collection_item")
    suspend fun getAllUserCollectionItems(): List<UserCollectionItemLegacy>

}
