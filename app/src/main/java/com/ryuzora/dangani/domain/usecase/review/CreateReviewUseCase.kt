package com.ryuzora.dangani.domain.usecase.review

import com.ryuzora.dangani.domain.model.Review
import com.ryuzora.dangani.domain.repository.ReviewRepository

class CreateReviewUseCase(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(review: Review): Result<Unit> {
        if (review.id.isBlank()) {
            return Result.failure(Exception("ID review tidak boleh kosong"))
        }

        if (review.taskId.isBlank()) {
            return Result.failure(Exception("Task tidak ditemukan"))
        }

        if (review.revieweeId.isBlank()) {
            return Result.failure(Exception("Helper tidak ditemukan"))
        }

        if (review.rating !in 1..5) {
            return Result.failure(Exception("Rating harus antara 1 sampai 5"))
        }

        if (review.comment.isBlank()) {
            return Result.failure(Exception("Komentar tidak boleh kosong"))
        }

        return reviewRepository.createReview(review)
    }
}