package com.alfaazplus.sunnah.repository.userdata

import androidx.core.text.parseAsHtml
import com.alfaazplus.sunnah.db.dao.HadithDao
import com.alfaazplus.sunnah.db.dao.UserDataDao
import com.alfaazplus.sunnah.db.models.userdata.ReadHistory
import com.alfaazplus.sunnah.db.models.userdata.UserBookmark
import com.alfaazplus.sunnah.db.models.userdata.UserCollection
import com.alfaazplus.sunnah.db.models.userdata.UserCollectionItem
import com.alfaazplus.sunnah.ui.models.userdata.ReadHistoryNormalized
import com.alfaazplus.sunnah.ui.models.userdata.UserBookmarkNormalized
import com.alfaazplus.sunnah.ui.models.userdata.UserCollectionItemNormalized
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

    fun loadCollectionsForHadith(hadithCollectionId: Int, hadithBookId: Int, hadithNumber: String): Flow<List<UserCollection>> {
        return dao.getUserCollectionsForHadith(hadithCollectionId, hadithBookId, hadithNumber)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeUserCollectionItems(collectionId: Long): Flow<List<UserCollectionItemNormalized>> {
        return dao
            .observeUserCollectionItems(collectionId)
            .distinctUntilChanged()
            .flatMapLatest { items ->
                if (items.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val flows: List<Flow<UserCollectionItemNormalized>> = items.map { item ->
                    val translation = hadithDao.getHadithTranslationByHadithNumber(
                        item.hadithCollectionId, item.hadithBookId, item.hadithNumber, "en"
                    )

                    val collectionName = hadithDao.getCollectionInfoById("en", item.hadithCollectionId).name


                    val translationText = translation.hadithText
                        .parseAsHtml()
                        .toAnnotatedString()

                    flowOf(
                        UserCollectionItemNormalized(
                            item = item,
                            translation = translation,
                            collectionName = collectionName,
                            translationText = translationText,
                        )
                    )
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeAllUserBookmarks(): Flow<List<UserBookmarkNormalized>> {
        return dao
            .observeUserBookmarks()
            .distinctUntilChanged()
            .flatMapLatest { items ->
                if (items.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val flows: List<Flow<UserBookmarkNormalized>> = items.map { item ->
                    val translation = hadithDao.getHadithTranslationByHadithNumber(
                        item.hadithCollectionId, item.hadithBookId, item.hadithNumber, "en"
                    )

                    val collectionName = hadithDao.getCollectionInfoById("en", item.hadithCollectionId).name


                    val translationText = translation.hadithText
                        .parseAsHtml()
                        .toAnnotatedString()

                    flowOf(
                        UserBookmarkNormalized(
                            item = item,
                            translation = translation,
                            collectionName = collectionName,
                            translationText = translationText,
                        )
                    )
                }

                combine(flows) { it.toList() }
            }
    }

    fun observeRecentUserBookmarks(): Flow<List<UserBookmark>> {
        return dao.observeRecentUserBookmarks()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeAllReadHistory(): Flow<List<ReadHistoryNormalized>> {
        return dao
            .observeReadHistory()
            .distinctUntilChanged()
            .flatMapLatest { items ->
                if (items.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val flows: List<Flow<ReadHistoryNormalized>> = items.map { item ->
                    val translation = hadithDao.getHadithTranslationByHadithNumber(
                        item.hadithCollectionId, item.hadithBookId, item.hadithNumber, "en"
                    )

                    val collectionName = hadithDao.getCollectionInfoById("en", item.hadithCollectionId).name


                    val translationText = translation.hadithText
                        .parseAsHtml()
                        .toAnnotatedString()

                    flowOf(
                        ReadHistoryNormalized(
                            item = item,
                            translation = translation,
                            collectionName = collectionName,
                            translationText = translationText,
                        )
                    )
                }

                combine(flows) { it.toList() }
            }
    }

    fun observeRecentReadHistory(): Flow<List<ReadHistory>> {
        return dao.observeRecentReadHistory()
    }

    suspend fun saveReadHistory(
        hadithCollectionId: Int,
        hadithBookId: Int,
        hadithNumber: String,
    ) {
        dao.upsertReadHistory(
            ReadHistory(
                hadithCollectionId = hadithCollectionId,
                hadithBookId = hadithBookId,
                hadithNumber = hadithNumber,
                createdAt = Date(),
            )
        )

        dao.deleteOldReadHistory(100)
    }

    suspend fun clearReadHistory() {
        dao.clearReadHistory()
    }
}