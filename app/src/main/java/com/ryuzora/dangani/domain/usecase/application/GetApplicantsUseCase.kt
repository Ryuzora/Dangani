package com.ryuzora.dangani.domain.usecase.application

import com.ryuzora.dangani.domain.model.TaskApplication
import com.ryuzora.dangani.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetApplicantsUseCase(private val taskRepository: TaskRepository) {
    operator fun invoke(taskId: String): Flow<List<TaskApplication>> {
        return taskRepository.getApplicants(taskId)
    }
}

