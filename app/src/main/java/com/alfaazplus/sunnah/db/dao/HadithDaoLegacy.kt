package com.alfaazplus.sunnah.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.alfaazplus.sunnah.db.entities.migration.LegacyHadithUrnLookup

@Deprecated("")
@Dao
interface HadithDaoLegacy {
    @Query(
        """
        SELECT collection_id, book_id, hadith_number, urn
        FROM hadith
        WHERE collection_id IN (:collectionIds)
        """
    )
    suspend fun getLegacyHadithUrnLookups(collectionIds: List<Int>): List<LegacyHadithUrnLookup>

    @Query("DELETE FROM collection WHERE collection_id = :collectionId")
    suspend fun deleteCollection(collectionId: Int)
}
