package com.alfaazplus.sunnah.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.alfaazplus.sunnah.db.models.scholars.Scholar

@Dao
interface ScholarsDao {
    @Query("SELECT * from scholars WHERE id IN (:ids) ORDER BY rank ASC")
    suspend fun getScholars(ids: List<Int>): List<Scholar>


    // search books in collectionIds
    @Query(
        """
            SELECT 
                *
            FROM scholars
            WHERE 
                short_name LIKE '%' || :query || '%'
                OR full_name LIKE '%' || :query || '%'
                OR kunya LIKE '%' || :query || '%'
                OR city LIKE '%' || :query || '%'
                OR birth_place LIKE '%' || :query || '%'
        """
    )
    fun searchScholars(query: String): PagingSource<Int, Scholar>
}