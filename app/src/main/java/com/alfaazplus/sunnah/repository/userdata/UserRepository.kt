package com.alfaazplus.sunnah.repository.userdata

import com.alfaazplus.sunnah.db.models.userdata.UserBookmark
import com.alfaazplus.sunnah.db.models.userdata.UserCollection
import com.alfaazplus.sunnah.db.models.userdata.UserCollectionItem
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun loadAllUserCollections(): Flow<List<UserCollection>>
    fun loadUserCollectionItems(collectionId: Long): Flow<List<UserCollectionItem>>
    suspend fun addUserCollection(userCollection: UserCollection): UserCollection
    suspend fun updateUserCollection(userCollection: UserCollection)
    suspend fun deleteUserCollection(id: Long)
    suspend fun addUserCollectionItem(userCollectionItem: UserCollectionItem): UserCollectionItem
    suspend fun updateUserCollectionItem(userCollectionItem: UserCollectionItem)
    suspend fun deleteUserCollectionItem(id: Long)
    suspend fun clearUserCollectionItems(collectionId: Long)
    fun loadAllUserBookmarks(): Flow<List<UserBookmark>>
    suspend fun addUserBookmark(userBookmark: UserBookmark): UserBookmark
    suspend fun updateUserBookmark(userBookmark: UserBookmark)
    suspend fun deleteUserBookmark(id: Long)
    suspend fun clearUserBookmarks()
}
