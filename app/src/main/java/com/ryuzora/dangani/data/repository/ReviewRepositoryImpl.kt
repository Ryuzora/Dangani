package com.ryuzora.dangani.data.repository

import com.ryuzora.dangani.data.remote.FirestoreService
import com.ryuzora.dangani.data.remote.dto.ReviewDto
import com.ryuzora.dangani.domain.model.Review
import com.ryuzora.dangani.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReviewRepositoryImpl(
    private val firestoreService: FirestoreService
) : ReviewRepository {

    override suspend fun createReview(review: Review): Result<Unit> {
        return try {
            val existingReview = firestoreService
                .queryCollection("reviews", "taskId", review.taskId)
                .first()
                .maxByOrNull { document ->
                    document.toObject(ReviewDto::class.java)?.createdAt ?: 0L
                }

            val reviewId = existingReview?.id ?: review.id
            val createdAt = existingReview
                ?.toObject(ReviewDto::class.java)
                ?.createdAt
                ?: review.createdAt

            val data = mapOf(
                "id" to reviewId,
                "reviewerId" to review.reviewerId,
                "reviewerName" to review.reviewerName,
                "reviewerAvatarUrl" to review.reviewerAvatarUrl,
                "revieweeId" to review.revieweeId,
                "taskId" to review.taskId,
                "rating" to review.rating,
                "comment" to review.comment,
                "createdAt" to createdAt
            )

            firestoreService.setDocument(
                collectionName = "reviews",
                documentId = reviewId,
                data = data
            )

            refreshUserRating(review.revieweeId)

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

    private suspend fun refreshUserRating(userId: String) {
        if (userId.isBlank()) return

        try {
            val reviews = firestoreService
                .queryCollection("reviews", "revieweeId", userId)
                .first()
                .mapNotNull { it.toObject(ReviewDto::class.java) }
                .latestReviewPerTask()

            val ratingAverage = if (reviews.isNotEmpty()) {
                reviews.map { it.rating }.average()
            } else {
                0.0
            }

            firestoreService.updateDocument(
                "users",
                userId,
                mapOf("ratingAverage" to ratingAverage)
            )
        } catch (_: Exception) { }
    }

    private fun List<ReviewDto>.latestReviewPerTask(): List<ReviewDto> {
        return groupBy { it.taskId.ifBlank { it.id } }
            .values
            .mapNotNull { reviews -> reviews.maxByOrNull { it.createdAt } }
    }
}

