package com.ryuzora.dangani.domain.usecase.task

import com.ryuzora.dangani.domain.model.Task
import com.ryuzora.dangani.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetMyTasksUseCase(private val taskRepository: TaskRepository) {
    operator fun invoke(userId: String, role: String): Flow<List<Task>> {
        return when (role) {
            "requester" -> taskRepository.getTasksByRequesterId(userId)
            "helper" -> taskRepository.getTasksByHelperId(userId)
            else -> taskRepository.getTasksByRequesterId(userId)
        }
    }
}

