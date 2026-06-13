package com.ryuzora.dangani.domain.usecase.application

import com.ryuzora.dangani.domain.repository.TaskRepository

class AcceptHelperUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskId: String, helperId: String): Result<Unit> {
        return taskRepository.acceptHelper(taskId, helperId)
    }
}

