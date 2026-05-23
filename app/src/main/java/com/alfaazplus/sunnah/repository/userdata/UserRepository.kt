package com.alfaazplus.sunnah.repository.userdata

import com.alfaazplus.sunnah.db.dao.HadithDao
import com.alfaazplus.sunnah.db.dao.UserDataDao
import com.alfaazplus.sunnah.db.entities.userdata.v2.ReadHistory
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserBookmark
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserCollection
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserCollectionItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.Date

class UserRepository(
    val hadithDao: HadithDao,
    val dao: UserDataDao,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeUserCollectionById(id: Long): Flow<UserCollection?> {
        return dao
            .observeUserCollectionById(id)
            .distinctUntilChanged()
            .flatMapLatest { collection ->
                if (collection == null) return@flatMapLatest flowOf(null)

                collection.itemsCount = dao.getUserCollectionItemsCount(collection.id)
                flowOf(collection)
            }
    }

    fun loadCollectionsForHadith(hadithId: String): Flow<List<UserCollection>> {
        return dao.getUserCollectionsForHadith(hadithId)
    }

    suspend fun addUserCollection(userCollection: UserCollection) {
        dao.createUserCollection(userCollection)
    }

    suspend fun updateUserCollection(userCollection: UserCollection) {
        dao.updateUserCollection(userCollection)
    }

    suspend fun deleteUserCollection(id: Long) {
        dao.deleteUserCollection(id)
    }

    suspend fun addUserCollectionItem(userCollectionItem: UserCollectionItem): UserCollectionItem {
        val existingItem = dao.getUserCollectionItem(
            userCollectionItem.userCollectionId,
            userCollectionItem.hadithId,
        )

        if (existingItem != null) {
            dao.updateUserCollectionItem(
                existingItem.copy(
                    remark = userCollectionItem.remark,
                    updatedAt = Date(),
                )
            )
            return dao.getUserCollectionItemById(existingItem.id)
        }

        val id = dao.createUserCollectionItem(userCollectionItem)
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
        hadithId: String,
    ) {
        dao.removeItemFromUserCollection(userCollectionId, hadithId)
    }

    suspend fun clearUserCollectionItems(collectionId: Long) {
        dao.clearUserCollectionItems(collectionId)
    }

    fun observeUserBookmark(hadithId: String): Flow<UserBookmark?> {
        return dao.observeUserBookmark(hadithId)
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

    suspend fun saveReadHistory(hadithId: String) {
        dao.upsertReadHistory(
            ReadHistory(
                hadithId = hadithId,
                createdAt = Date(),
            )
        )

        dao.deleteOldReadHistory(100)
    }

    suspend fun clearReadHistory() {
        dao.clearReadHistory()
    }
}
