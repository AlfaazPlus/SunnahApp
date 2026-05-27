package com.alfaazplus.sunnah.db.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import androidx.room3.Upsert
import com.alfaazplus.sunnah.db.entities.userdata.v2.ReadHistory
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserBookmark
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserCollectionItem
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserCollection
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserCollectionItemsCount
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface UserDataDao {
    @Query("SELECT * FROM user_collections ORDER BY updated_at DESC")
    fun observeUserCollections(): Flow<List<UserCollection>>

    @Query("SELECT COUNT(id) FROM user_collection_items WHERE collection_id = :collectionId")
    suspend  fun getUserCollectionItemsCount(collectionId: Long): Int

    @Query("""
        SELECT collection_id AS id, COUNT(id) AS count
        FROM user_collection_items
        WHERE collection_id IN (:collectionIds)
        GROUP BY collection_id
    """)
    suspend fun getUserCollectionItemCounts(collectionIds: List<Long>): List<UserCollectionItemsCount>

    @Query("SELECT * FROM user_collections WHERE id = :id")
    fun observeUserCollectionById(id: Long): Flow<UserCollection?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createUserCollection(userCollection: UserCollection): Long

    @Update
    suspend fun updateUserCollection(userCollection: UserCollection)

    @Query(
        """
            UPDATE user_collections SET updated_at = :updatedAt  WHERE id in (:ids)
        """
    )
    suspend fun updateUserCollectionsTimestamp(ids: List<Long>, updatedAt: Date)

    @Query("DELETE FROM user_collections WHERE id = :id")
    suspend fun deleteUserCollection(id: Long)

    @Query(
        """
        SELECT uc.* FROM user_collections uc
        JOIN user_collection_items uci ON uc.id = uci.collection_id
        WHERE uci.hadith_id = :hadithId
        """
    )
    fun getUserCollectionsForHadith(hadithId: String): Flow<List<UserCollection>>

    @Query(
        """
        SELECT * FROM user_collection_items
        WHERE collection_id = :collectionId
        ORDER BY updated_at DESC
        """
    )
    fun observeUserCollectionItems(collectionId: Long): Flow<List<UserCollectionItem>>

    @Query("SELECT * FROM user_collection_items WHERE id = :id")
    suspend fun getUserCollectionItemById(id: Long): UserCollectionItem

    @Query(
        """
        SELECT * FROM user_collection_items
        WHERE collection_id = :userCollectionId AND hadith_id = :hadithId
        """
    )
    suspend fun getUserCollectionItem(
        userCollectionId: Long,
        hadithId: String,
    ): UserCollectionItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createUserCollectionItem(userCollectionItem: UserCollectionItem): Long

    @Update
    suspend fun updateUserCollectionItem(userCollectionItem: UserCollectionItem)

    @Query("DELETE FROM user_collection_items WHERE id = :id")
    suspend fun deleteUserCollectionItem(id: Long)

    @Query(
        """
        DELETE FROM user_collection_items
        WHERE collection_id IN (:userCollectionIds) AND hadith_id = :hadithId
        """
    )
    suspend fun removeItemFromUserCollections(
        userCollectionIds: List<Long>,
        hadithId: String,
    )

    @Query("DELETE FROM user_collection_items WHERE collection_id = :collectionId")
    suspend fun clearUserCollectionItems(collectionId: Long)

    @Query("SELECT * FROM user_bookmarks ORDER BY updated_at DESC LIMIT 10")
    fun observeRecentUserBookmarks(): Flow<List<UserBookmark>>

    @Query("SELECT * FROM user_bookmarks ORDER BY updated_at DESC")
    fun observeUserBookmarks(): Flow<List<UserBookmark>>

    @Query("SELECT * FROM user_bookmarks WHERE hadith_id = :hadithId")
    fun observeUserBookmark(hadithId: String): Flow<UserBookmark?>

    @Query("SELECT * FROM user_bookmarks WHERE id = :id")
    suspend fun getUserBookmarkById(id: Long): UserBookmark

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createUserBookmark(bookmark: UserBookmark): Long

    @Update
    suspend fun updateUserBookmark(bookmark: UserBookmark)

    @Query("DELETE FROM user_bookmarks WHERE id = :id")
    suspend fun deleteUserBookmark(id: Long)

    @Query("DELETE FROM user_bookmarks")
    suspend fun clearUserBookmarks()

    @Query("SELECT * FROM read_history ORDER BY created_at DESC LIMIT 10")
    fun observeRecentReadHistory(): Flow<List<ReadHistory>>

    @Query("SELECT * FROM read_history ORDER BY created_at DESC")
    fun observeReadHistory(): Flow<List<ReadHistory>>

    @Upsert
    suspend fun upsertReadHistory(readHistory: ReadHistory)

    @Query("DELETE FROM read_history")
    suspend fun clearReadHistory()

    @Query("DELETE FROM read_history WHERE hadith_id IN (:hadithIds)")
    suspend fun deleteReadHistoryForHadithIds(hadithIds: List<String>)

    @Query(
        """
        DELETE FROM read_history
        WHERE rowid NOT IN (
            SELECT rowid FROM read_history
            ORDER BY created_at DESC
            LIMIT :keepCount
        )
        """
    )
    suspend fun deleteOldReadHistory(keepCount: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserCollections(collections: List<UserCollection>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserCollectionItems(items: List<UserCollectionItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserBookmarks(bookmarks: List<UserBookmark>)

    @Upsert
    suspend fun upsertReadHistory(entries: List<ReadHistory>)
}
