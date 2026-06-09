package com.ryuzora.dangani.domain.usecase.task

import com.ryuzora.dangani.domain.model.Task
import com.ryuzora.dangani.domain.repository.TaskRepository

class DeleteTaskUseCase(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(task: Task): Result<Unit> {
        if (!task.isDeletable) {
            return Result.failure(IllegalStateException("Tugas tidak dapat dihapus karena bukti kerja sudah dikirim"))
        }
        return taskRepository.deleteTask(task.id)
    }
}
