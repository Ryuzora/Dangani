package com.ryuzora.dangani.domain.usecase.application

import com.ryuzora.dangani.domain.repository.TaskRepository

class ApplyToTaskUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskId: String, helperId: String, requesterId: String): Result<Unit> {
        if (helperId == requesterId) {
            return Result.failure(IllegalArgumentException("Tidak dapat melamar tugas sendiri"))
        }
        return taskRepository.applyToTask(taskId, helperId)
    }
}

