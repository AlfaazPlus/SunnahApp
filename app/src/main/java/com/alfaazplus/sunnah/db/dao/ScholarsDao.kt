package com.alfaazplus.sunnah.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alfaazplus.sunnah.db.models.scholars.Scholar

@Dao
interface ScholarsDao {
    @Query("SELECT * from scholars WHERE id IN (:ids) ORDER BY rank ASC")
    suspend fun getScholars(ids: List<Int>): List<Scholar>
}