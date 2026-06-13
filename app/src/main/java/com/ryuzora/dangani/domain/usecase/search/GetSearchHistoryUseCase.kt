package com.ryuzora.dangani.domain.usecase.search

import com.ryuzora.dangani.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class GetSearchHistoryUseCase(private val searchRepository: SearchRepository) {
    operator fun invoke(userId: String): Flow<List<String>> {
        return searchRepository.getRecentSearches(userId)
    }
}

