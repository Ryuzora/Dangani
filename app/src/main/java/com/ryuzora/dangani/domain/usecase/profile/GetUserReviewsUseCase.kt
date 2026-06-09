package com.ryuzora.dangani.domain.usecase.profile

import com.ryuzora.dangani.domain.model.Review
import com.ryuzora.dangani.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class GetUserReviewsUseCase(private val searchRepository: SearchRepository) {
    operator fun invoke(userId: String): Flow<List<Review>> {
        return searchRepository.getReviewsForUser(userId)
    }
}
