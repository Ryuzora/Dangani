package com.ryuzora.dangani.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ryuzora.dangani.data.local.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Query("SELECT * FROM reviews WHERE revieweeId = :revieweeId ORDER BY createdAt DESC")
    fun getByRevieweeId(revieweeId: String): Flow<List<ReviewEntity>>

    @Query("SELECT AVG(CAST(rating AS REAL)) FROM reviews WHERE revieweeId = :revieweeId")
    fun getAverageRating(revieweeId: String): Flow<Double?>

    @Query("SELECT COUNT(*) FROM reviews WHERE revieweeId = :revieweeId")
    fun getReviewCount(revieweeId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reviews: List<ReviewEntity>)

    @Query("DELETE FROM reviews WHERE id = :reviewId")
    suspend fun deleteById(reviewId: String)
}
