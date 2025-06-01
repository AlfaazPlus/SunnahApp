package com.alfaazplus.sunnah.repository.userdata

import com.alfaazplus.sunnah.db.dao.UserDataDao
import com.alfaazplus.sunnah.db.models.userdata.UserBookmark
import com.alfaazplus.sunnah.db.models.userdata.UserCollection
import com.alfaazplus.sunnah.db.models.userdata.UserCollectionItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.Date

class UserRepository(
    private val dao: UserDataDao,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeAllUserCollections(): Flow<List<UserCollection>> {
        return dao
            .observeUserCollections()
            .flatMapLatest { collections ->
                if (collections.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val flows: List<Flow<UserCollection>> = collections.map { collection ->
                    dao
                        .observeUserCollectionItemsCount(collection.id)
                        .map { count ->
                            collection.itemsCount = flowOf(count)
                            collection
                        }
                }

                combine(flows) { it.toList() }
            }
    }

    fun loadCollectionsForHadith(hadithCollectionId: Int, hadithBookId: Int, hadithNumber: String): Flow<List<UserCollection>> {
        return dao.getUserCollectionsForHadith(hadithCollectionId, hadithBookId, hadithNumber)
    }

    fun loadUserCollectionItems(collectionId: Long): Flow<List<UserCollectionItem>> {
        return dao.getUserCollectionItems(collectionId)
    }

    suspend fun addUserCollection(userCollection: UserCollection): UserCollection {
        val id = dao.createUserCollection(userCollection)
        return dao.getUserCollectionById(id)
    }

    suspend fun updateUserCollection(userCollection: UserCollection) {
        dao.updateUserCollection(userCollection)
    }

    suspend fun deleteUserCollection(id: Long) {
        dao.deleteUserCollection(id)
    }

    suspend fun addUserCollectionItem(userCollectionItem: UserCollectionItem): UserCollectionItem {
        val existingItem = userCollectionItem.let {
            dao.getUserCollectionItem(
                it.userCollectionId, it.hadithCollectionId, it.hadithBookId, it.hadithNumber
            )
        }

        val id = if (existingItem !== null) {
            dao.updateUserCollectionItem(
                existingItem.copy(
                    remark = userCollectionItem.remark,
                    updatedAt = Date(),
                )
            )
            userCollectionItem.id
        } else {
            dao.createUserCollectionItem(userCollectionItem)
        }

        return dao.getUserCollectionItemById(id)
    }

    suspend fun updateUserCollectionItem(userCollectionItem: UserCollectionItem) {
        dao.updateUserCollectionItem(userCollectionItem)
    }

    suspend fun deleteUserCollectionItem(id: Long) {
        dao.deleteUserCollectionItem(id)
    }

    suspend fun removeItemFromUserCollection(
        userCollectionId: Long,
        hadithCollectionId: Int,
        hadithBookId: Int,
        hadithNumber: String,
    ) {
        dao.removeItemFromUserCollection(
            userCollectionId, hadithCollectionId, hadithBookId, hadithNumber
        )
    }

    suspend fun clearUserCollectionItems(collectionId: Long) {
        dao.clearUserCollectionItems(collectionId)
    }

    fun observeAllUserBookmarks(): Flow<List<UserBookmark>> {
        return dao.getUserBookmarks()
    }

    fun observeUserBookmark(
        hadithCollectionId: Int,
        hadithBookId: Int,
        hadithNumber: String,
    ): Flow<UserBookmark?> {
        return dao.observeUserBookmark(hadithCollectionId, hadithBookId, hadithNumber)
    }

    suspend fun addUserBookmark(userBookmark: UserBookmark): UserBookmark {
        val id = dao.createUserBookmark(userBookmark)
        return dao.getUserBookmarkById(id)
    }

    suspend fun updateUserBookmark(userBookmark: UserBookmark) {
        dao.updateUserBookmark(userBookmark)
    }

    suspend fun deleteUserBookmark(id: Long) {
        dao.deleteUserBookmark(id)
    }

    suspend fun clearUserBookmarks() {
        dao.clearUserBookmarks()
    }
}