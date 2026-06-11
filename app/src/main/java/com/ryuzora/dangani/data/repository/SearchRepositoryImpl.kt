package com.ryuzora.dangani.data.repository

import com.ryuzora.dangani.data.local.dao.FavoriteDao
import com.ryuzora.dangani.data.local.dao.ReviewDao
import com.ryuzora.dangani.data.local.dao.SearchHistoryDao
import com.ryuzora.dangani.data.local.entity.FavoriteEntity
import com.ryuzora.dangani.data.local.entity.SearchHistoryEntity
import com.ryuzora.dangani.data.mapper.toDomain
import com.ryuzora.dangani.data.mapper.toEntity
import com.ryuzora.dangani.data.mapper.toFirestoreMap
import com.ryuzora.dangani.data.remote.FirestoreService
import com.ryuzora.dangani.data.remote.dto.ReviewDto
import com.ryuzora.dangani.data.remote.dto.UserDto
import com.ryuzora.dangani.domain.model.Review
import com.ryuzora.dangani.domain.repository.SearchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchRepositoryImpl(
    private val searchHistoryDao: SearchHistoryDao,
    private val favoriteDao: FavoriteDao,
    private val reviewDao: ReviewDao,
    private val firestoreService: FirestoreService
) : SearchRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // --- Search History (Room only) ---

    override fun getRecentSearches(userId: String): Flow<List<String>> {
        return searchHistoryDao.getByUserId(userId).map { entities ->
            entities.map { it.query }
        }
    }

    override suspend fun saveSearchQuery(userId: String, query: String) {
        searchHistoryDao.insert(
            SearchHistoryEntity(
                userId = userId,
                query = query,
                searchedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun clearSearchHistory(userId: String) {
        searchHistoryDao.deleteByUserId(userId)
    }

    // --- Favorites (Room only) ---

    override fun getFavoriteTaskIds(userId: String): Flow<List<String>> {
        return favoriteDao.getByUserId(userId).map { entities ->
            entities.map { it.taskId }
        }
    }

    override fun isTaskFavorite(userId: String, taskId: String): Flow<Boolean> {
        return favoriteDao.isFavorite(userId, taskId)
    }

    override suspend fun addFavorite(userId: String, taskId: String) {
        favoriteDao.insert(
            FavoriteEntity(
                userId = userId,
                taskId = taskId,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun removeFavorite(userId: String, taskId: String) {
        favoriteDao.delete(userId, taskId)
    }

    // --- Reviews (Firestore + Room) ---

    override fun getReviewsForUser(userId: String): Flow<List<Review>> {
        // Sync from Firestore in background
        coroutineScope.launch {
            try {
                firestoreService.queryCollection("reviews", "revieweeId", userId).collect { docs ->
                    val entities = docs.mapNotNull { doc ->
                        val dto = doc.toObject(ReviewDto::class.java)
                        dto?.apply {
                            id = doc.id
                            if ((reviewerName.isBlank() || reviewerAvatarUrl.isBlank()) && reviewerId.isNotBlank()) {
                                val reviewer = firestoreService
                                    .getDocument("users", reviewerId)
                                    ?.toObject(UserDto::class.java)

                                if (reviewerName.isBlank()) {
                                    reviewerName = reviewer?.username.orEmpty()
                                }
                                if (reviewerAvatarUrl.isBlank()) {
                                    reviewerAvatarUrl = reviewer?.avatarUrl.orEmpty()
                                }
                            }
                        }?.toEntity()
                    }
                    reviewDao.insertAll(entities)
                }
            } catch (_: Exception) { }
        }
        return reviewDao.getByRevieweeId(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addReview(review: Review): Result<Unit> {
        return try {
            val reviewMap = review.toFirestoreMap().toMutableMap()
            val docId = firestoreService.addDocument("reviews", reviewMap)
            firestoreService.updateDocument("reviews", docId, mapOf("id" to docId))

            val savedReview = review.copy(id = docId)
            reviewDao.insert(savedReview.toEntity())

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
