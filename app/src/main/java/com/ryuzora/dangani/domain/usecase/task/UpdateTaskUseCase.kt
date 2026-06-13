package com.ryuzora.dangani.domain.usecase.task

import com.ryuzora.dangani.domain.model.Task
import com.ryuzora.dangani.domain.repository.TaskRepository

class UpdateTaskUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(task: Task): Result<Unit> {
        if (!task.isEditable) {
            return Result.failure(IllegalStateException("Tugas tidak dapat diedit karena bukti kerja sudah dikirim"))
        }
        return taskRepository.updateTask(task)
    }
}

