package com.ryuzora.dangani.domain.repository

import com.ryuzora.dangani.domain.model.Task
import com.ryuzora.dangani.domain.model.TaskApplication
import com.ryuzora.dangani.domain.model.TaskCategory
import com.ryuzora.dangani.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<Task>>
    fun getTaskById(taskId: String): Flow<Task?>
    fun getTasksByRequesterId(requesterId: String): Flow<List<Task>>
    fun getTasksByHelperId(helperId: String): Flow<List<Task>>
    fun getTasksByCategory(category: TaskCategory): Flow<List<Task>>
    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>>
    fun searchTasks(query: String): Flow<List<Task>>

    suspend fun createTask(task: Task): Result<String>
    suspend fun updateTask(task: Task): Result<Unit>
    suspend fun deleteTask(taskId: String): Result<Unit>

    // Applications
    fun getApplicants(taskId: String): Flow<List<TaskApplication>>
    suspend fun applyToTask(taskId: String, helperId: String): Result<Unit>
    suspend fun acceptHelper(taskId: String, helperId: String): Result<Unit>
    suspend fun hasUserApplied(taskId: String, userId: String): Flow<Boolean>

    // Submissions
    suspend fun submitWork(taskId: String, fileUri: String): Result<String>
    suspend fun acceptWork(taskId: String): Result<Unit>
    suspend fun requestRevision(taskId: String): Result<Unit>
}

