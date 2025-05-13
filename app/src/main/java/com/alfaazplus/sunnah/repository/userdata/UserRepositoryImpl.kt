package com.alfaazplus.sunnah.repository.userdata

import com.alfaazplus.sunnah.db.dao.UserDataDao
import com.alfaazplus.sunnah.db.models.userdata.UserBookmark
import com.alfaazplus.sunnah.db.models.userdata.UserCollection
import com.alfaazplus.sunnah.db.models.userdata.UserCollectionItem
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(
    private val dao: UserDataDao,
) : UserRepository {
    override fun loadAllUserCollections(): Flow<List<UserCollection>> {
        return dao.getUserCollections()
    }

    override fun loadUserCollectionItems(collectionId: Long): Flow<List<UserCollectionItem>> {
        return dao.getUserCollectionItems(collectionId)
    }

    override suspend fun addUserCollection(userCollection: UserCollection): UserCollection {
        val id =  dao.createUserCollection(userCollection)
        return dao.getUserCollectionById(id)
    }

    override suspend fun updateUserCollection(userCollection: UserCollection) {
        dao.updateUserCollection(userCollection)
    }

    override suspend fun deleteUserCollection(id: Long) {
        dao.deleteUserCollection(id)
    }

    override suspend fun addUserCollectionItem(userCollectionItem: UserCollectionItem): UserCollectionItem {
        val id = dao.createUserCollectionItem(userCollectionItem)
        return dao.getUserCollectionItemById(id)
    }

    override suspend fun updateUserCollectionItem(userCollectionItem: UserCollectionItem) {
        dao.updateUserCollectionItem(userCollectionItem)
    }

    override suspend fun deleteUserCollectionItem(id: Long) {
        dao.deleteUserCollectionItem(id)
    }

    override suspend fun clearUserCollectionItems(collectionId: Long) {
        dao.clearUserCollectionItems(collectionId)
    }

    override fun loadAllUserBookmarks(): Flow<List<UserBookmark>> {
        return dao.getUserBookmarks()
    }

    override suspend fun addUserBookmark(userBookmark: UserBookmark): UserBookmark {
        val id = dao.createUserBookmark(userBookmark)
        return dao.getUserBookmarkById(id)
    }

    override suspend fun updateUserBookmark(userBookmark: UserBookmark) {
        dao.updateUserBookmark(userBookmark)
    }

    override suspend fun deleteUserBookmark(id: Long) {
        dao.deleteUserBookmark(id)
    }

    override suspend fun clearUserBookmarks() {
        dao.clearUserBookmarks()
    }
}