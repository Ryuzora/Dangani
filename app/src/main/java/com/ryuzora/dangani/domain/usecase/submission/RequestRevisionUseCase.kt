package com.ryuzora.dangani.domain.usecase.submission

import com.ryuzora.dangani.domain.repository.TaskRepository

class RequestRevisionUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskId: String, revisionNote: String): Result<Unit> {
        return taskRepository.requestRevision(taskId, revisionNote)
    }
}

