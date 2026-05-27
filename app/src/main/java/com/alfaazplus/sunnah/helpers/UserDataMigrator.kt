package com.alfaazplus.sunnah.helpers

import androidx.room3.withWriteTransaction
import com.alfaazplus.sunnah.Logger
import com.alfaazplus.sunnah.db.dao.HadithDao
import com.alfaazplus.sunnah.db.dao.HadithDaoLegacy
import com.alfaazplus.sunnah.db.dao.UserDataDao
import com.alfaazplus.sunnah.db.dao.UserDataDaoLegacy
import com.alfaazplus.sunnah.db.databases.UserDatabase
import com.alfaazplus.sunnah.db.entities.userdata.ReadHistoryLegacy
import com.alfaazplus.sunnah.db.entities.userdata.UserBookmarkLegacy
import com.alfaazplus.sunnah.db.entities.userdata.UserCollectionItemLegacy
import com.alfaazplus.sunnah.db.entities.userdata.UserCollectionLegacy
import com.alfaazplus.sunnah.db.entities.userdata.v2.ReadHistory
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserBookmark
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserCollection
import com.alfaazplus.sunnah.db.entities.userdata.v2.UserCollectionItem
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

private const val INSERT_CHUNK_SIZE = 500
private const val URN_LOOKUP_CHUNK_SIZE = 900

private data class LegacyHadithRef(
    val collectionId: Int,
    val bookId: Int,
    val hadithNumber: String,
) {
    fun cacheKey(): String = "$collectionId-$bookId-$hadithNumber"
}

data class MigrationStats(
    var readHistoryTotal: Int = 0,
    var readHistoryMigrated: Int = 0,
    var readHistorySkipped: Int = 0,
    var bookmarksTotal: Int = 0,
    var bookmarksMigrated: Int = 0,
    var bookmarksSkipped: Int = 0,
    var collectionsTotal: Int = 0,
    var collectionsMigrated: Int = 0,
    var collectionItemsTotal: Int = 0,
    var collectionItemsMigrated: Int = 0,
    var collectionItemsSkipped: Int = 0,
    var missingLegacyUrn: Int = 0,
    var missingV2HadithId: Int = 0,
    var urnParseErrors: Int = 0,
)

object UserDataMigrator {
    suspend fun migrate(
        legacyUserDao: UserDataDaoLegacy,
        legacyHadithDao: HadithDaoLegacy,
        hadithDaoV2: HadithDao,
        userDatabaseV2: UserDatabase,
    ): MigrationStats {
        val stats = MigrationStats()
        val userDaoV2 = userDatabaseV2.dao

        val legacyData = coroutineScope {
            val collectionsDeferred = async { legacyUserDao.getAllUserCollections() }
            val itemsDeferred = async { legacyUserDao.getAllUserCollectionItems() }
            val bookmarksDeferred = async { legacyUserDao.getAllUserBookmarks() }
            val historyDeferred = async { legacyUserDao.getAllReadHistory() }

            LegacyUserDataSnapshot(
                collections = collectionsDeferred.await(),
                items = itemsDeferred.await(),
                bookmarks = bookmarksDeferred.await(),
                history = historyDeferred.await(),
            )
        }

        Logger.d("LegacyUserDataSnapshot", legacyData)

        val legacyCollections = legacyData.collections
        val legacyItems = legacyData.items
        val legacyBookmarks = legacyData.bookmarks
        val legacyHistory = legacyData.history

        stats.collectionsTotal = legacyCollections.size
        stats.collectionItemsTotal = legacyItems.size
        stats.bookmarksTotal = legacyBookmarks.size
        stats.readHistoryTotal = legacyHistory.size

        if (legacyCollections.isEmpty() && legacyItems.isEmpty() && legacyBookmarks.isEmpty() && legacyHistory.isEmpty()) {
            Logger.d("UserDataMigrator: no legacy user data")
            return stats
        }

        val uniqueRefs = collectUniqueRefs(legacyItems, legacyBookmarks, legacyHistory)
        val hadithIdByRefKey = resolveHadithIds(uniqueRefs, legacyHadithDao, hadithDaoV2, stats)

        Logger.d(hadithIdByRefKey)

        userDatabaseV2.withWriteTransaction {
            val collectionIdMap = migrateCollections(legacyCollections, userDaoV2, stats)
            migrateCollectionItems(legacyItems, collectionIdMap, hadithIdByRefKey, userDaoV2, stats)
            migrateBookmarks(legacyBookmarks, hadithIdByRefKey, userDaoV2, stats)
            migrateReadHistory(legacyHistory, hadithIdByRefKey, userDaoV2, stats)
        }

        logSummary(stats)
        return stats
    }

    private data class LegacyUserDataSnapshot(
        val collections: List<UserCollectionLegacy>,
        val items: List<UserCollectionItemLegacy>,
        val bookmarks: List<UserBookmarkLegacy>,
        val history: List<ReadHistoryLegacy>,
    )

    private fun collectUniqueRefs(
        items: List<UserCollectionItemLegacy>,
        bookmarks: List<UserBookmarkLegacy>,
        history: List<ReadHistoryLegacy>,
    ): Set<LegacyHadithRef> {
        val refs = LinkedHashSet<LegacyHadithRef>(items.size + bookmarks.size + history.size)

        for (item in items) {
            refs.add(
                LegacyHadithRef(
                    item.hadithCollectionId,
                    item.hadithBookId,
                    item.hadithNumber,
                )
            )
        }

        for (bookmark in bookmarks) {
            refs.add(
                LegacyHadithRef(
                    bookmark.hadithCollectionId,
                    bookmark.hadithBookId,
                    bookmark.hadithNumber,
                )
            )
        }

        for (entry in history) {
            refs.add(
                LegacyHadithRef(
                    entry.hadithCollectionId,
                    entry.hadithBookId,
                    entry.hadithNumber,
                )
            )
        }

        return refs
    }

    private suspend fun resolveHadithIds(
        refs: Set<LegacyHadithRef>,
        legacyHadithDao: HadithDaoLegacy,
        hadithDaoV2: HadithDao,
        stats: MigrationStats,
    ): Map<String, String> {
        if (refs.isEmpty()) return emptyMap()

        val neededKeys = refs.associateBy { it.cacheKey() }
        val urnByRefKey = HashMap<String, Long>(neededKeys.size)

        val legacyRows = legacyHadithDao.getLegacyHadithUrnLookups(getLegacyIncludedCollectionIds())

        for (row in legacyRows) {
            val key = LegacyHadithRef(row.collectionId, row.bookId, row.hadithNumber).cacheKey()
            if (key !in neededKeys || key in urnByRefKey) continue

            val urn = row.urn.toLongOrNull()
            if (urn == null) {
                stats.urnParseErrors++
                continue
            }

            urnByRefKey[key] = urn
        }

        stats.missingLegacyUrn = neededKeys.size - urnByRefKey.size

        val uniqueUrns = urnByRefKey.values
            .filter { it > 0 }
            .toSet()

        val hadithIdByUrn = HashMap<Long, String>(uniqueUrns.size)

        for (chunk in uniqueUrns.chunked(URN_LOOKUP_CHUNK_SIZE)) {
            for (row in hadithDaoV2.getHadithIdsByUrns(chunk)) {
                hadithIdByUrn[row.urn] = row.hadithId
            }
        }

        val hadithIdByRefKey = HashMap<String, String>(urnByRefKey.size)
        for ((key, urn) in urnByRefKey) {
            val hadithId = hadithIdByUrn[urn]

            if (hadithId.isNullOrBlank()) {
                stats.missingV2HadithId++
                continue
            }

            hadithIdByRefKey[key] = hadithId
        }

        return hadithIdByRefKey
    }

    private suspend fun migrateCollections(
        legacyCollections: List<UserCollectionLegacy>,
        userDaoV2: UserDataDao,
        stats: MigrationStats,
    ): Map<Long, Long> {
        if (legacyCollections.isEmpty()) return emptyMap()

        val v2Collections = legacyCollections.map { legacy ->
            UserCollection(
                name = legacy.name,
                description = legacy.description,
                color = legacy.color,
                createdAt = legacy.createdAt,
                updatedAt = legacy.updatedAt,
            )
        }

        val newIds = userDaoV2.insertUserCollections(v2Collections)
        stats.collectionsMigrated = newIds.size

        return legacyCollections.indices.associate { index ->
            legacyCollections[index].id to newIds[index]
        }
    }

    private suspend fun migrateCollectionItems(
        legacyItems: List<UserCollectionItemLegacy>,
        collectionIdMap: Map<Long, Long>,
        hadithIdByRefKey: Map<String, String>,
        userDaoV2: UserDataDao,
        stats: MigrationStats,
    ) {
        val v2Items = ArrayList<UserCollectionItem>(legacyItems.size)

        for (item in legacyItems) {
            val refKey = LegacyHadithRef(
                item.hadithCollectionId,
                item.hadithBookId,
                item.hadithNumber,
            ).cacheKey()
            val hadithId = hadithIdByRefKey[refKey]
            val newCollectionId = collectionIdMap[item.userCollectionId]

            if (hadithId == null || newCollectionId == null) {
                stats.collectionItemsSkipped++
                continue
            }

            v2Items.add(
                UserCollectionItem(
                    userCollectionId = newCollectionId,
                    hadithId = hadithId,
                    remark = item.remark,
                    createdAt = item.createdAt,
                    updatedAt = item.updatedAt,
                )
            )
        }

        insertInChunks(v2Items) { chunk -> userDaoV2.insertUserCollectionItems(chunk) }
        stats.collectionItemsMigrated = v2Items.size
    }

    private suspend fun migrateBookmarks(
        legacyBookmarks: List<UserBookmarkLegacy>,
        hadithIdByRefKey: Map<String, String>,
        userDaoV2: UserDataDao,
        stats: MigrationStats,
    ) {
        val seenHadithIds = HashSet<String>(legacyBookmarks.size)
        val v2Bookmarks = ArrayList<UserBookmark>(legacyBookmarks.size)

        for (bookmark in legacyBookmarks) {
            val refKey = LegacyHadithRef(
                bookmark.hadithCollectionId,
                bookmark.hadithBookId,
                bookmark.hadithNumber,
            ).cacheKey()

            val hadithId = hadithIdByRefKey[refKey]

            if (hadithId == null) {
                stats.bookmarksSkipped++
                continue
            }

            if (!seenHadithIds.add(hadithId)) {
                stats.bookmarksSkipped++
                continue
            }

            v2Bookmarks.add(
                UserBookmark(
                    hadithId = hadithId,
                    remark = bookmark.remark,
                    createdAt = bookmark.createdAt,
                    updatedAt = bookmark.updatedAt,
                )
            )
        }

        insertInChunks(v2Bookmarks) { chunk -> userDaoV2.insertUserBookmarks(chunk) }

        stats.bookmarksMigrated = v2Bookmarks.size
    }

    private suspend fun migrateReadHistory(
        legacyHistory: List<ReadHistoryLegacy>,
        hadithIdByRefKey: Map<String, String>,
        userDaoV2: UserDataDao,
        stats: MigrationStats,
    ) {
        val v2History = ArrayList<ReadHistory>(legacyHistory.size)

        for (entry in legacyHistory) {
            val refKey = LegacyHadithRef(
                entry.hadithCollectionId,
                entry.hadithBookId,
                entry.hadithNumber,
            ).cacheKey()
            val hadithId = hadithIdByRefKey[refKey]

            if (hadithId == null) {
                stats.readHistorySkipped++
                continue
            }

            v2History.add(
                ReadHistory(
                    hadithId = hadithId,
                    createdAt = entry.createdAt,
                )
            )
        }

        insertInChunks(v2History) { chunk -> userDaoV2.upsertReadHistory(chunk) }

        stats.readHistoryMigrated = v2History.size
    }

    private suspend fun <T> insertInChunks(
        items: List<T>,
        insert: suspend (List<T>) -> Unit,
    ) {
        if (items.isEmpty()) return

        for (chunk in items.chunked(INSERT_CHUNK_SIZE)) {
            insert(chunk)
        }
    }

    private fun logSummary(stats: MigrationStats) {
        Logger.d(
            "UserDataMigrator: finished — " + "collections ${stats.collectionsMigrated}/${stats.collectionsTotal}, " + "items ${stats.collectionItemsMigrated}/${stats.collectionItemsTotal} " + "(skipped ${stats.collectionItemsSkipped}), " + "bookmarks ${stats.bookmarksMigrated}/${stats.bookmarksTotal} " + "(skipped ${stats.bookmarksSkipped}), " + "history ${stats.readHistoryMigrated}/${stats.readHistoryTotal} " + "(skipped ${stats.readHistorySkipped}), " + "missingLegacyUrn=${stats.missingLegacyUrn}, " + "missingV2HadithId=${stats.missingV2HadithId}, " + "urnParseErrors=${stats.urnParseErrors}",
        )
    }
}
