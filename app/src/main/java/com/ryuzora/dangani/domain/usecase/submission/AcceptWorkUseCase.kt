package com.ryuzora.dangani.domain.usecase.submission

import com.ryuzora.dangani.domain.repository.TaskRepository

class AcceptWorkUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskId: String): Result<Unit> {
        return taskRepository.acceptWork(taskId)
    }
}
