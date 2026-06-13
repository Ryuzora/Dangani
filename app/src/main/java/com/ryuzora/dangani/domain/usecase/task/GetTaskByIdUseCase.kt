package com.ryuzora.dangani.domain.usecase.task

import com.ryuzora.dangani.domain.model.Task
import com.ryuzora.dangani.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTaskByIdUseCase(private val taskRepository: TaskRepository) {
    operator fun invoke(taskId: String): Flow<Task?> {
        return taskRepository.getTaskById(taskId)
    }
}

