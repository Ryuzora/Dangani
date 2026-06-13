package com.ryuzora.dangani.presentation.viewmodel.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryuzora.dangani.DanganiApplication
import com.ryuzora.dangani.data.remote.FirebaseAuthService
import com.ryuzora.dangani.data.remote.FirebaseStorageService
import com.ryuzora.dangani.data.remote.FirestoreService
import com.ryuzora.dangani.data.repository.NotificationRepositoryImpl
import com.ryuzora.dangani.data.repository.TaskRepositoryImpl
import com.ryuzora.dangani.data.repository.UserRepositoryImpl
import com.ryuzora.dangani.domain.model.Task
import com.ryuzora.dangani.domain.model.User
import com.ryuzora.dangani.domain.usecase.application.ApplyToTaskUseCase
import com.ryuzora.dangani.domain.usecase.profile.GetUserProfileUseCase
import com.ryuzora.dangani.domain.usecase.task.GetTaskByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TaskDetailUiState(
    val task: Task? = null,
    val requester: User? = null,
    val hasApplied: Boolean = false,
    val isCurrentUserRequester: Boolean = false,
    val isCurrentUserHelper: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

class TaskDetailViewModel(private val taskId: String) : ViewModel() {

    private val db = DanganiApplication.instance.database
    private val firebaseAuth = FirebaseAuthService()
    private val firestoreService = FirestoreService()
    private val storageService = FirebaseStorageService()

    private val notificationRepo = NotificationRepositoryImpl(db.notificationDao(), firestoreService)
    private val taskRepo = TaskRepositoryImpl(db.taskDao(), db.taskApplicationDao(), firestoreService, storageService, notificationRepo)
    private val userRepo = UserRepositoryImpl(db.userDao(), firebaseAuth, firestoreService, storageService)

    private val getTaskByIdUseCase = GetTaskByIdUseCase(taskRepo)
    private val getUserProfileUseCase = GetUserProfileUseCase(userRepo)
    private val applyToTaskUseCase = ApplyToTaskUseCase(taskRepo)

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getTaskByIdUseCase(taskId).collect { task ->
                if (task != null) {
                    val currentUserId = firebaseAuth.getCurrentUserId()
                    val isRequester = currentUserId == task.requesterId
                    val isHelper = currentUserId == task.helperId

                    _uiState.update {
                        it.copy(
                            task = task,
                            isCurrentUserRequester = isRequester,
                            isCurrentUserHelper = isHelper,
                            isLoading = false
                        )
                    }

                    // Load requester profile
                    launch {
                        getUserProfileUseCase(task.requesterId).collect { user ->
                            _uiState.update { it.copy(requester = user) }
                        }
                    }

                    // Check if user has applied
                    if (!isRequester && currentUserId != null) {
                        launch {
                            taskRepo.hasUserApplied(taskId, currentUserId).collect { applied ->
                                _uiState.update { it.copy(hasApplied = applied) }
                            }
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Tugas tidak ditemukan")
                    }
                }
            }
        }
    }

    fun applyToTask() {
        val task = _uiState.value.task ?: return
        val currentUserId = firebaseAuth.getCurrentUserId() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            applyToTaskUseCase(taskId, currentUserId, task.requesterId)
                .onSuccess {
                    _uiState.update { it.copy(hasApplied = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
        }
    }
}


