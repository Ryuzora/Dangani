package com.ryuzora.dangani.domain.repository

import com.ryuzora.dangani.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    suspend fun createReview(review: Review): Result<Unit>

    fun getReviewByTaskId(taskId: String): Flow<Review?>
}