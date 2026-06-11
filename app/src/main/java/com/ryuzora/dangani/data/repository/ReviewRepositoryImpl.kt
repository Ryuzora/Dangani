package com.ryuzora.dangani.data.repository

import com.ryuzora.dangani.data.remote.FirestoreService
import com.ryuzora.dangani.domain.model.Review
import com.ryuzora.dangani.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReviewRepositoryImpl(
    private val firestoreService: FirestoreService
) : ReviewRepository {

    override suspend fun createReview(review: Review): Result<Unit> {
        return try {
            val data = mapOf(
                "id" to review.id,
                "reviewerId" to review.reviewerId,
                "reviewerName" to review.reviewerName,
                "reviewerAvatarUrl" to review.reviewerAvatarUrl,
                "revieweeId" to review.revieweeId,
                "taskId" to review.taskId,
                "rating" to review.rating,
                "comment" to review.comment,
                "createdAt" to review.createdAt
            )

            firestoreService.setDocument(
                collectionName = "reviews",
                documentId = review.id,
                data = data
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getReviewByTaskId(taskId: String): Flow<Review?> {
        return firestoreService
            .queryCollection(
                collectionName = "reviews",
                field = "taskId",
                value = taskId
            )
            .map { documents ->
                documents.firstOrNull()?.toObject(Review::class.java)
            }
    }
}