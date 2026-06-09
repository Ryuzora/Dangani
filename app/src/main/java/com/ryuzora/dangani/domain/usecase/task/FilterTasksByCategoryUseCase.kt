package com.ryuzora.dangani.domain.usecase.task

import com.ryuzora.dangani.domain.model.Task
import com.ryuzora.dangani.domain.model.TaskCategory
import com.ryuzora.dangani.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class FilterTasksByCategoryUseCase(private val taskRepository: TaskRepository) {
    operator fun invoke(category: TaskCategory): Flow<List<Task>> {
        return if (category == TaskCategory.ALL) {
            taskRepository.getAllTasks()
        } else {
            taskRepository.getTasksByCategory(category)
        }
    }
}
