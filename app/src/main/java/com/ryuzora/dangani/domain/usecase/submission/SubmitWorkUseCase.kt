package com.ryuzora.dangani.domain.usecase.submission

import com.ryuzora.dangani.domain.repository.TaskRepository

class SubmitWorkUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskId: String, fileUri: String): Result<String> {
        return taskRepository.submitWork(taskId, fileUri)
    }
}

