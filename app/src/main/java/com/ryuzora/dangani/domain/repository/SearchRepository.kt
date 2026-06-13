package com.ryuzora.dangani.domain.repository

import com.ryuzora.dangani.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    // Search History (Room only)
    fun getRecentSearches(userId: String): Flow<List<String>>
    suspend fun saveSearchQuery(userId: String, query: String)
    suspend fun clearSearchHistory(userId: String)

    // Favorites (Room only)
    fun getFavoriteTaskIds(userId: String): Flow<List<String>>
    fun isTaskFavorite(userId: String, taskId: String): Flow<Boolean>
    suspend fun addFavorite(userId: String, taskId: String)
    suspend fun removeFavorite(userId: String, taskId: String)

    // Reviews
    fun getReviewsForUser(userId: String): Flow<List<Review>>
    suspend fun addReview(review: Review): Result<Unit>
}

