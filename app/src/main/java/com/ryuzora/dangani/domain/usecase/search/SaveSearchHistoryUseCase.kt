package com.ryuzora.dangani.domain.usecase.search

import com.ryuzora.dangani.domain.repository.SearchRepository

class SaveSearchHistoryUseCase(private val searchRepository: SearchRepository) {
    suspend operator fun invoke(userId: String, query: String) {
        searchRepository.saveSearchQuery(userId, query)
    }
}
