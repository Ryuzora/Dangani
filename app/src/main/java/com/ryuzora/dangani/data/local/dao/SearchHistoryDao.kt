package com.ryuzora.dangani.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ryuzora.dangani.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM search_history WHERE userId = :userId ORDER BY searchedAt DESC LIMIT 10")
    fun getByUserId(userId: String): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchHistory: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)

    @Query("DELETE FROM search_history")
    suspend fun deleteAll()
}

