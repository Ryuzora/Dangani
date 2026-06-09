package com.ryuzora.dangani.domain.usecase.task

import com.ryuzora.dangani.domain.model.Task
import com.ryuzora.dangani.domain.model.TaskCategory
import com.ryuzora.dangani.domain.model.TaskPoints
import com.ryuzora.dangani.domain.repository.TaskRepository
import com.ryuzora.dangani.domain.repository.UserRepository

class CreateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        category: TaskCategory,
        points: Int
    ): Result<String> {
        if (title.isBlank()) {
            return Result.failure(IllegalArgumentException("Judul tugas tidak boleh kosong"))
        }
        if (description.isBlank()) {
            return Result.failure(IllegalArgumentException("Deskripsi tugas tidak boleh kosong"))
        }
        val validPoints = TaskPoints.entries.map { it.value }
        if (points !in validPoints) {
            return Result.failure(IllegalArgumentException("Poin tugas tidak valid"))
        }

        val currentUserId = userRepository.getCurrentUserId()
            ?: return Result.failure(IllegalStateException("User belum login"))

        val task = Task(
            title = title,
            description = description,
            category = category,
            taskPoints = points,
            requesterId = currentUserId
        )
        return taskRepository.createTask(task)
    }
}
