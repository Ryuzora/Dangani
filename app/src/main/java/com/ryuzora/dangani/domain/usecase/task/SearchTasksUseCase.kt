package com.ryuzora.dangani.domain.usecase.task

import com.ryuzora.dangani.domain.model.Task
import com.ryuzora.dangani.domain.repository.SearchRepository
import com.ryuzora.dangani.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class SearchTasksUseCase(
    private val taskRepository: TaskRepository,
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(userId: String, query: String): Flow<List<Task>> {
        if (query.isNotBlank()) {
            searchRepository.saveSearchQuery(userId, query)
        }
        return taskRepository.searchTasks(query)
    }
}

