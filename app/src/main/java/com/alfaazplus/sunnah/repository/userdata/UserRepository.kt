package com.alfaazplus.sunnah.repository.userdata

import com.alfaazplus.sunnah.db.dao.HadithDao
import com.alfaazplus.sunnah.db.dao.UserDataDao
import com.alfaazplus.sunnah.db.entities.userdata.v2.ReadHistory
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserBookmark
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserCollectionItem
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserCollection
import com.alfaazplus.sunnah.ui.models.userdata.ReadHistoryNormalized
import com.alfaazplus.sunnah.ui.models.userdata.UserBookmarkNormalized
import com.alfaazplus.sunnah.ui.models.userdata.UserCollectionItemNormalized
import com.alfaazplus.sunnah.ui.utils.composable.tryOrNull
import com.alfaazplus.sunnah.ui.utils.text.toAnnotatedString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.Date

class UserRepository(
    private val hadithDao: HadithDao,
    private val dao: UserDataDao,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeAllUserCollections(): Flow<List<UserCollection>> {
        return dao
            .observeUserCollections()
            .distinctUntilChanged()
            .flatMapLatest { collections ->
                if (collections.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val flows: List<Flow<UserCollection>> = collections.map { collection ->
                    collection.itemsCount = dao.observeUserCollectionItemsCount(collection.id)
                    flowOf(collection)
                }

                combine(flows) { it.toList() }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeUserCollectionById(id: Long): Flow<UserCollection?> {
        return dao
            .observeUserCollectionById(id)
            .distinctUntilChanged()
            .flatMapLatest { collection ->
                if (collection == null) return@flatMapLatest flowOf(null)

                collection.itemsCount = dao.observeUserCollectionItemsCount(collection.id)
                flowOf(collection)
            }
    }

    fun loadCollectionsForHadith(hadithId: String): Flow<List<UserCollection>> {
        return dao.getUserCollectionsForHadith(hadithId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeUserCollectionItems(collectionId: Long): Flow<List<UserCollectionItemNormalized>> {
        return dao
            .observeUserCollectionItems(collectionId)
            .distinctUntilChanged()
            .flatMapLatest { items ->
                if (items.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val flows: List<Flow<UserCollectionItemNormalized>> = items.map { item ->
                    flowOf(normalizeCollectionItem(item))
                }

                combine(flows) { it.toList() }
            }
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeAllUserBookmarks(): Flow<List<UserBookmarkNormalized>> {
        return dao
            .observeUserBookmarks()
            .distinctUntilChanged()
            .flatMapLatest { items ->
                if (items.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val flows: List<Flow<UserBookmarkNormalized>> = items.map { item ->
                    flowOf(normalizeBookmark(item))
                }

                combine(flows) { it.toList() }
            }
    }

    fun observeRecentUserBookmarks(): Flow<List<UserBookmark>> {
        return dao.observeRecentUserBookmarks()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeAllReadHistory(): Flow<List<ReadHistoryNormalized>> {
        return dao
            .observeReadHistory()
            .distinctUntilChanged()
            .flatMapLatest { items ->
                if (items.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val flows: List<Flow<ReadHistoryNormalized>> = items.map { item ->
                    flowOf(normalizeReadHistory(item))
                }

                combine(flows) { it.toList() }
            }
    }

    fun observeRecentReadHistory(): Flow<List<ReadHistory>> {
        return dao.observeRecentReadHistory()
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

    suspend fun resolveHadithIdFromUrn(urn: String): String? {
        val urnValue = urn.toLongOrNull() ?: return null
        return hadithDao.getHadithIdByUrn(urnValue)
    }

    suspend fun clearUserDataForCollection(collectionId: String) {
        val hadithIds = hadithDao.getHadithIdsForCollection(collectionId)
        if (hadithIds.isEmpty()) return

        for (chunk in hadithIds.chunked(900)) {
            dao.deleteReadHistoryForHadithIds(chunk)
        }
    }

    private suspend fun normalizeCollectionItem(item: UserCollectionItem): UserCollectionItemNormalized {
        val hwc = tryOrNull { hadithDao.getHadithById(item.hadithId) }
        val collectionName = hwc?.let {
            tryOrNull { hadithDao.getCollectionById(it.collectionId)?.getTitle("en") }
        }
        val translationText = hwc?.plainTranslationText()?.toAnnotatedString()

        return UserCollectionItemNormalized(
            item = item,
            hadith = hwc,
            collectionName = collectionName,
            displayNumber = hwc?.displayNumber() ?: item.hadithId,
            translationText = translationText,
        )
    }

    private suspend fun normalizeBookmark(item: UserBookmark): UserBookmarkNormalized {
        val hwc = tryOrNull { hadithDao.getHadithById(item.hadithId) }
        val collectionName = hwc?.let {
            tryOrNull { hadithDao.getCollectionById(it.collectionId)?.getTitle("en") }
        }
        val translationText = hwc?.plainTranslationText()?.toAnnotatedString()

        return UserBookmarkNormalized(
            item = item,
            hadith = hwc,
            collectionName = collectionName,
            displayNumber = hwc?.displayNumber() ?: item.hadithId,
            translationText = translationText,
        )
    }

    private suspend fun normalizeReadHistory(item: ReadHistory): ReadHistoryNormalized {
        val hwc = tryOrNull { hadithDao.getHadithById(item.hadithId) }
        val collectionName = hwc?.let {
            tryOrNull { hadithDao.getCollectionById(it.collectionId)?.getTitle("en") }
        }
        val translationText = hwc?.plainTranslationText()?.toAnnotatedString()

        return ReadHistoryNormalized(
            item = item,
            hadith = hwc,
            collectionName = collectionName,
            displayNumber = hwc?.displayNumber() ?: item.hadithId,
            translationText = translationText,
        )
    }
}
