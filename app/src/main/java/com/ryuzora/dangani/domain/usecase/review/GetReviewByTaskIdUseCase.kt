package com.ryuzora.dangani.domain.usecase.review

import com.ryuzora.dangani.domain.model.Review
import com.ryuzora.dangani.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow

class GetReviewByTaskIdUseCase(
    private val reviewRepository: ReviewRepository
) {
    operator fun invoke(taskId: String): Flow<Review?> {
        return reviewRepository.getReviewByTaskId(taskId)
    }
}
