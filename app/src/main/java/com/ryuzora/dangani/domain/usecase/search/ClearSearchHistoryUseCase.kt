package com.ryuzora.dangani.domain.usecase.search

import com.ryuzora.dangani.domain.repository.SearchRepository

class ClearSearchHistoryUseCase(private val searchRepository: SearchRepository) {
    suspend operator fun invoke(userId: String) {
        searchRepository.clearSearchHistory(userId)
    }
}

