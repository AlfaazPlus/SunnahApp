package com.alfaazplus.sunnah.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.alfaazplus.sunnah.db.models.userdata.ReadHistory
import com.alfaazplus.sunnah.db.models.userdata.UserBookmark
import com.alfaazplus.sunnah.db.models.userdata.UserCollection
import com.alfaazplus.sunnah.db.models.userdata.UserCollectionItem
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao {
    @Query("SELECT * from user_collection ORDER BY updated_at DESC")
    fun observeUserCollections(): Flow<List<UserCollection>>

    @Query("SELECT COUNT(id) FROM user_collection_item WHERE u_collection_id = :collectionId")
    fun observeUserCollectionItemsCount(collectionId: Long): Flow<Int>

    @Query("SELECT * from user_collection WHERE id = :id")
    fun observeUserCollectionById(id: Long): Flow<UserCollection?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createUserCollection(userCollection: UserCollection): Long

    @Update
    suspend fun updateUserCollection(userCollection: UserCollection)

    @Query("DELETE FROM user_collection WHERE id = :id")
    suspend fun deleteUserCollection(id: Long)

    @Query(
        """
        SELECT uc.* FROM user_collection uc
        JOIN user_collection_item uci ON uc.id = uci.u_collection_id
        WHERE uci.h_collection_id = :hadithCollectionId AND uci.h_book_id = :hadithBookId AND uci.hadith_number = :hadithNumber
    """
    )
    fun getUserCollectionsForHadith(
        hadithCollectionId: Int,
        hadithBookId: Int,
        hadithNumber: String,
    ): Flow<List<UserCollection>>


    @Query(
        """
        SELECT * from user_collection_item
        WHERE u_collection_id = :collectionId
        ORDER BY updated_at DESC
    """
    )
    fun observeUserCollectionItems(collectionId: Long): Flow<List<UserCollectionItem>>

    @Query("SELECT * from user_collection_item WHERE id = :id")
    suspend fun getUserCollectionItemById(id: Long): UserCollectionItem

    // is in collection
    @Query(
        """
        SELECT * FROM user_collection_item
        WHERE u_collection_id = :userCollectionId AND h_collection_id = :hadithCollectionId
        AND h_book_id = :hadithBookId AND hadith_number = :hadithNumber
    """
    )
    suspend fun getUserCollectionItem(
        userCollectionId: Long,
        hadithCollectionId: Int,
        hadithBookId: Int,
        hadithNumber: String,
    ): UserCollectionItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createUserCollectionItem(userCollectionItem: UserCollectionItem): Long

    @Update
    suspend fun updateUserCollectionItem(userCollectionItem: UserCollectionItem)

    @Query("DELETE FROM user_collection_item WHERE id = :id")
    suspend fun deleteUserCollectionItem(id: Long)

    @Query(
        """
        DELETE FROM user_collection_item
        WHERE u_collection_id = :userCollectionId AND h_collection_id = :hadithCollectionId
        AND h_book_id = :hadithBookId AND hadith_number = :hadithNumber
    """
    )
    suspend fun removeItemFromUserCollection(
        userCollectionId: Long,
        hadithCollectionId: Int,
        hadithBookId: Int,
        hadithNumber: String,
    )

    @Query("DELETE FROM user_collection_item WHERE u_collection_id = :collectionId")
    suspend fun clearUserCollectionItems(collectionId: Long)

    @Query("SELECT * from user_bookmark ORDER BY updated_at DESC LIMIT 10")
    fun observeRecentUserBookmarks(): Flow<List<UserBookmark>>

    @Query("SELECT * from user_bookmark ORDER BY updated_at DESC")
    fun observeUserBookmarks(): Flow<List<UserBookmark>>

    @Query("SELECT * from user_bookmark WHERE h_collection_id = :hadithCollectionId AND h_book_id = :hadithBookId AND hadith_number = :hadithNumber")
    fun observeUserBookmark(
        hadithCollectionId: Int,
        hadithBookId: Int,
        hadithNumber: String,
    ): Flow<UserBookmark?>

    @Query("SELECT * from user_bookmark WHERE id = :id")
    suspend fun getUserBookmarkById(id: Long): UserBookmark

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createUserBookmark(bookmark: UserBookmark): Long

    @Update
    suspend fun updateUserBookmark(bookmark: UserBookmark)

    @Query("DELETE FROM user_bookmark WHERE id = :id")
    suspend fun deleteUserBookmark(id: Long)

    @Query("DELETE FROM user_bookmark")
    suspend fun clearUserBookmarks()

    @Query("SELECT * from read_history ORDER BY created_at DESC LIMIT 10")
    fun observeRecentReadHistory(): Flow<List<ReadHistory>>


    @Query("SELECT * from read_history ORDER BY created_at DESC")
    fun observeReadHistory(): Flow<List<ReadHistory>>

    @Upsert
    suspend fun upsertReadHistory(readHistory: ReadHistory)

    @Query("DELETE FROM read_history")
    suspend fun clearReadHistory()

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

    // delete read history for collection
    @Query("DELETE FROM read_history WHERE h_collection_id = :collectionId")
    suspend fun deleteReadHistoryForCollection(collectionId: Int)

}