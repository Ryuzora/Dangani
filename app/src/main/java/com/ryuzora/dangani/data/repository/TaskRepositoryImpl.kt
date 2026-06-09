package com.ryuzora.dangani.data.repository

import android.net.Uri
import com.ryuzora.dangani.data.local.dao.TaskApplicationDao
import com.ryuzora.dangani.data.local.dao.TaskDao
import com.ryuzora.dangani.data.local.entity.TaskApplicationEntity
import com.ryuzora.dangani.data.mapper.toDomain
import com.ryuzora.dangani.data.mapper.toEntity
import com.ryuzora.dangani.data.mapper.toFirestoreMap
import com.ryuzora.dangani.data.remote.FirebaseStorageService
import com.ryuzora.dangani.data.remote.FirestoreService
import com.ryuzora.dangani.data.remote.dto.TaskApplicationDto
import com.ryuzora.dangani.data.remote.dto.TaskDto
import com.ryuzora.dangani.data.remote.dto.UserDto
import com.ryuzora.dangani.domain.model.Notification
import com.ryuzora.dangani.domain.model.NotificationType
import com.ryuzora.dangani.domain.model.Task
import com.ryuzora.dangani.domain.model.TaskApplication
import com.ryuzora.dangani.domain.model.TaskCategory
import com.ryuzora.dangani.domain.model.TaskStatus
import com.ryuzora.dangani.domain.repository.NotificationRepository
import com.ryuzora.dangani.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val taskApplicationDao: TaskApplicationDao,
    private val firestoreService: FirestoreService,
    private val storageService: FirebaseStorageService,
    private val notificationRepository: NotificationRepository
) : TaskRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun getAllTasks(): Flow<List<Task>> {
        // Sync from Firestore in background
        coroutineScope.launch {
            try {
                firestoreService.getCollection("tasks").collect { docs ->
                    val entities = docs.mapNotNull { doc ->
                        val dto = doc.toObject(TaskDto::class.java)
                        dto?.apply { id = doc.id }?.toEntity()
                    }
                    taskDao.insertAll(entities)
                }
            } catch (_: Exception) { }
        }
        return taskDao.getAll().map { entities -> entities.map { it.toDomain() } }
    }

    override fun getTaskById(taskId: String): Flow<Task?> {
        // Refresh from Firestore in background
        coroutineScope.launch {
            try {
                val doc = firestoreService.getDocument("tasks", taskId)
                if (doc != null && doc.exists()) {
                    val dto = doc.toObject(TaskDto::class.java)
                    if (dto != null) {
                        dto.id = taskId
                        taskDao.insert(dto.toEntity())
                    }
                }
            } catch (_: Exception) { }
        }
        return taskDao.getById(taskId).map { it?.toDomain() }
    }

    override fun getTasksByRequesterId(requesterId: String): Flow<List<Task>> {
        coroutineScope.launch {
            try {
                firestoreService.queryCollection("tasks", "requesterId", requesterId).collect { docs ->
                    val entities = docs.mapNotNull { doc ->
                        doc.toObject(TaskDto::class.java)?.apply { id = doc.id }?.toEntity()
                    }
                    taskDao.insertAll(entities)
                }
            } catch (_: Exception) { }
        }
        return taskDao.getByRequesterId(requesterId).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getTasksByHelperId(helperId: String): Flow<List<Task>> {
        coroutineScope.launch {
            try {
                firestoreService.queryCollection("tasks", "helperId", helperId).collect { docs ->
                    val entities = docs.mapNotNull { doc ->
                        doc.toObject(TaskDto::class.java)?.apply { id = doc.id }?.toEntity()
                    }
                    taskDao.insertAll(entities)
                }
            } catch (_: Exception) { }
        }
        return taskDao.getByHelperId(helperId).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getTasksByCategory(category: TaskCategory): Flow<List<Task>> {
        coroutineScope.launch {
            try {
                firestoreService.queryCollection("tasks", "category", category.name).collect { docs ->
                    val entities = docs.mapNotNull { doc ->
                        doc.toObject(TaskDto::class.java)?.apply { id = doc.id }?.toEntity()
                    }
                    taskDao.insertAll(entities)
                }
            } catch (_: Exception) { }
        }
        return taskDao.getByCategory(category.name).map { entities -> entities.map { it.toDomain() } }
    }

    override fun getTasksByStatus(status: TaskStatus): Flow<List<Task>> {
        coroutineScope.launch {
            try {
                firestoreService.queryCollection("tasks", "status", status.name).collect { docs ->
                    val entities = docs.mapNotNull { doc ->
                        doc.toObject(TaskDto::class.java)?.apply { id = doc.id }?.toEntity()
                    }
                    taskDao.insertAll(entities)
                }
            } catch (_: Exception) { }
        }
        return taskDao.getByStatus(status.name).map { entities -> entities.map { it.toDomain() } }
    }

    override fun searchTasks(query: String): Flow<List<Task>> {
        return taskDao.searchByTitle(query).map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun createTask(task: Task): Result<String> {
        return try {
            val taskMap = task.toFirestoreMap().toMutableMap()
            val docId = firestoreService.addDocument("tasks", taskMap)

            // Update Firestore doc with its own ID
            firestoreService.updateDocument("tasks", docId, mapOf("id" to docId))

            val savedTask = task.copy(id = docId)
            taskDao.insert(savedTask.toEntity())

            Result.success(docId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            val updatedTask = task.copy(updatedAt = System.currentTimeMillis())
            firestoreService.updateDocument("tasks", task.id, updatedTask.toFirestoreMap())
            taskDao.update(updatedTask.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            firestoreService.deleteDocument("tasks", taskId)
            taskDao.deleteById(taskId)
            taskApplicationDao.deleteByTaskId(taskId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Applications ---

    override fun getApplicants(taskId: String): Flow<List<TaskApplication>> {
        coroutineScope.launch {
            try {
                firestoreService.queryCollection("task_applications", "taskId", taskId).collect { docs ->
                    val entities = docs.mapNotNull { doc ->
                        doc.toObject(TaskApplicationDto::class.java)?.let { dto ->
                            dto.id = doc.id
                            TaskApplicationEntity(
                                id = dto.id,
                                taskId = dto.taskId,
                                helperId = dto.helperId,
                                helperName = dto.helperName,
                                helperAvatarUrl = dto.helperAvatarUrl,
                                helperRating = dto.helperRating,
                                helperTasksCompleted = dto.helperTasksCompleted,
                                helperIsTopHelper = dto.helperIsTopHelper,
                                status = dto.status,
                                appliedAt = dto.appliedAt
                            )
                        }
                    }
                    taskApplicationDao.insertAll(entities)
                }
            } catch (_: Exception) { }
        }
        return taskApplicationDao.getByTaskId(taskId)
            .distinctUntilChanged()
            .map { entities ->
            // Filter only pending applications to avoid showing accepted ones
            entities.filter { it.status == "pending" }.distinctBy { it.helperId }.map { entity ->
                TaskApplication(
                    id = entity.id,
                    taskId = entity.taskId,
                    helperId = entity.helperId,
                    helperName = entity.helperName,
                    helperAvatarUrl = entity.helperAvatarUrl,
                    helperRating = entity.helperRating,
                    helperTasksCompleted = entity.helperTasksCompleted,
                    helperIsTopHelper = entity.helperIsTopHelper,
                    status = entity.status,
                    appliedAt = entity.appliedAt
                )
            }
        }
    }

    override suspend fun applyToTask(taskId: String, helperId: String): Result<Unit> {
        return try {
            // Fetch helper info
            val helperDoc = firestoreService.getDocument("users", helperId)
            val helperDto = helperDoc?.toObject(UserDto::class.java)

            val applicationData = mapOf(
                "taskId" to taskId,
                "helperId" to helperId,
                "helperName" to (helperDto?.username ?: ""),
                "helperAvatarUrl" to (helperDto?.avatarUrl ?: ""),
                "helperRating" to (helperDto?.ratingAverage ?: 0.0),
                "helperTasksCompleted" to (helperDto?.tasksCompleted ?: 0),
                "helperIsTopHelper" to false,
                "status" to "pending",
                "appliedAt" to System.currentTimeMillis()
            )

            val docId = firestoreService.addDocument("task_applications", applicationData)
            firestoreService.updateDocument("task_applications", docId, mapOf("id" to docId))

            // Update applicant count on task
            val taskDoc = firestoreService.getDocument("tasks", taskId)
            if (taskDoc == null || !taskDoc.exists()) {
                return Result.failure(Exception("Tugas tidak ditemukan di server. Mungkin tugas ini sudah dihapus."))
            }
            
            val taskDto = taskDoc.toObject(TaskDto::class.java)
            val newCount = (taskDto?.applicantCount ?: 0) + 1
            firestoreService.updateDocument("tasks", taskId, mapOf("applicantCount" to newCount))

            // Save application to Room
            taskApplicationDao.insert(
                TaskApplicationEntity(
                    id = docId,
                    taskId = taskId,
                    helperId = helperId,
                    helperName = helperDto?.username ?: "",
                    helperAvatarUrl = helperDto?.avatarUrl ?: "",
                    helperRating = helperDto?.ratingAverage ?: 0.0,
                    helperTasksCompleted = helperDto?.tasksCompleted ?: 0,
                    helperIsTopHelper = false,
                    status = "pending",
                    appliedAt = System.currentTimeMillis()
                )
            )

            // Create notification for requester
            if (taskDto != null) {
                val notification = Notification(
                    userId = taskDto.requesterId,
                    role = "requester",
                    type = NotificationType.NEW_APPLICANT,
                    title = NotificationType.NEW_APPLICANT.displayName,
                    message = "${helperDto?.username ?: "Seseorang"} melamar tugas \"${taskDto.title}\"",
                    relatedTaskId = taskId,
                    relatedTaskTitle = taskDto.title,
                    senderName = helperDto?.username ?: "",
                    senderAvatarUrl = helperDto?.avatarUrl ?: ""
                )
                notificationRepository.createNotification(notification)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acceptHelper(taskId: String, helperId: String): Result<Unit> {
        return try {
            // Fetch helper info
            val helperDoc = firestoreService.getDocument("users", helperId)
            val helperDto = helperDoc?.toObject(UserDto::class.java)

            // Update task
            val updates = mapOf(
                "status" to TaskStatus.IN_PROGRESS.name,
                "helperId" to helperId,
                "helperName" to (helperDto?.username ?: ""),
                "helperAvatarUrl" to (helperDto?.avatarUrl ?: ""),
                "updatedAt" to System.currentTimeMillis()
            )
            firestoreService.updateDocument("tasks", taskId, updates)

            // Update Room
            val taskDoc = firestoreService.getDocument("tasks", taskId)
            val taskDto = taskDoc?.toObject(TaskDto::class.java)
            if (taskDto != null) {
                taskDto.id = taskId
                taskDao.insert(taskDto.toEntity())
            }

            // Notification for helper
            val notification = Notification(
                userId = helperId,
                role = "helper",
                type = NotificationType.APPLICATION_ACCEPTED,
                title = NotificationType.APPLICATION_ACCEPTED.displayName,
                message = "Lamaranmu untuk tugas \"${taskDto?.title ?: ""}\" diterima!",
                relatedTaskId = taskId,
                relatedTaskTitle = taskDto?.title ?: "",
                senderName = taskDto?.requesterName ?: "",
                senderAvatarUrl = taskDto?.requesterAvatarUrl ?: ""
            )
            notificationRepository.createNotification(notification)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun hasUserApplied(taskId: String, userId: String): Flow<Boolean> {
        return taskApplicationDao.getByTaskIdAndHelperId(taskId, userId).map { it != null }
    }

    // --- Submissions ---

    override suspend fun submitWork(taskId: String, driveLink: String): Result<String> {
        return try {
            val updates = mapOf(
                "proofOfWorkUrl" to driveLink,
                "status" to TaskStatus.NEED_REVIEW.name,
                "updatedAt" to System.currentTimeMillis()
            )
            firestoreService.updateDocument("tasks", taskId, updates)

            // Update Room
            val taskDoc = firestoreService.getDocument("tasks", taskId)
            val taskDto = taskDoc?.toObject(TaskDto::class.java)
            if (taskDto != null) {
                taskDto.id = taskId
                taskDao.insert(taskDto.toEntity())

                // Notification for requester
                val notification = Notification(
                    userId = taskDto.requesterId,
                    role = "requester",
                    type = NotificationType.WORK_SUBMITTED,
                    title = NotificationType.WORK_SUBMITTED.displayName,
                    message = "${taskDto.helperName} telah mengirim bukti pengerjaan untuk \"${taskDto.title}\"",
                    relatedTaskId = taskId,
                    relatedTaskTitle = taskDto.title,
                    senderName = taskDto.helperName,
                    senderAvatarUrl = taskDto.helperAvatarUrl
                )
                notificationRepository.createNotification(notification)
            }

            Result.success(driveLink)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acceptWork(taskId: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "status" to TaskStatus.ACCEPTED.name,
                "updatedAt" to System.currentTimeMillis()
            )
            firestoreService.updateDocument("tasks", taskId, updates)

            // Update Room
            val taskDoc = firestoreService.getDocument("tasks", taskId)
            val taskDto = taskDoc?.toObject(TaskDto::class.java)
            if (taskDto != null) {
                taskDto.id = taskId
                taskDao.insert(taskDto.toEntity())

                // Notification for helper
                val notification = Notification(
                    userId = taskDto.helperId,
                    role = "helper",
                    type = NotificationType.WORK_ACCEPTED,
                    title = NotificationType.WORK_ACCEPTED.displayName,
                    message = "Pekerjaanmu untuk \"${taskDto.title}\" telah disetujui!",
                    relatedTaskId = taskId,
                    relatedTaskTitle = taskDto.title,
                    senderName = taskDto.requesterName,
                    senderAvatarUrl = taskDto.requesterAvatarUrl
                )
                notificationRepository.createNotification(notification)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun requestRevision(taskId: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "status" to TaskStatus.REVISION.name,
                "proofOfWorkUrl" to "",
                "updatedAt" to System.currentTimeMillis()
            )
            firestoreService.updateDocument("tasks", taskId, updates)

            // Update Room
            val taskDoc = firestoreService.getDocument("tasks", taskId)
            val taskDto = taskDoc?.toObject(TaskDto::class.java)
            if (taskDto != null) {
                taskDto.id = taskId
                taskDao.insert(taskDto.toEntity())

                // Notification for helper
                val notification = Notification(
                    userId = taskDto.helperId,
                    role = "helper",
                    type = NotificationType.WORK_REVISION,
                    title = NotificationType.WORK_REVISION.displayName,
                    message = "Tugas \"${taskDto.title}\" membutuhkan revisi",
                    relatedTaskId = taskId,
                    relatedTaskTitle = taskDto.title,
                    senderName = taskDto.requesterName,
                    senderAvatarUrl = taskDto.requesterAvatarUrl
                )
                notificationRepository.createNotification(notification)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
