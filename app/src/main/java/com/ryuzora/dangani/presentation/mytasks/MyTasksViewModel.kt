package com.ryuzora.dangani.presentation.mytasks

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
import com.ryuzora.dangani.domain.usecase.task.GetMyTasksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyTasksUiState(
    val requesterTasks: List<Task> = emptyList(),
    val helperTasks: List<Task> = emptyList(),
    val selectedTab: Int = 0, // 0 = requester, 1 = helper
    val isLoading: Boolean = true
)

class MyTasksViewModel : ViewModel() {

    private val db = DanganiApplication.instance.database
    private val firebaseAuth = FirebaseAuthService()
    private val firestoreService = FirestoreService()
    private val storageService = FirebaseStorageService()

    private val userRepo = UserRepositoryImpl(db.userDao(), firebaseAuth, firestoreService, storageService)
    private val notificationRepo = NotificationRepositoryImpl(db.notificationDao(), firestoreService)
    private val taskRepo = TaskRepositoryImpl(db.taskDao(), db.taskApplicationDao(), firestoreService, storageService, notificationRepo)

    private val getMyTasksUseCase = GetMyTasksUseCase(taskRepo)

    private val _uiState = MutableStateFlow(MyTasksUiState())
    val uiState: StateFlow<MyTasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        val userId = userRepo.getCurrentUserId() ?: return

        // Load requester tasks
        viewModelScope.launch {
            getMyTasksUseCase(userId, "requester")
                .catch { /* silently handle errors, show empty list */ }
                .collect { tasks ->
                    _uiState.update {
                        it.copy(
                            requesterTasks = tasks.sortedByDescending { task -> task.updatedAt },
                            isLoading = false
                        )
                    }
                }
        }

        // Load helper tasks
        viewModelScope.launch {
            getMyTasksUseCase(userId, "helper")
                .catch { /* silently handle errors, show empty list */ }
                .collect { tasks ->
                    _uiState.update {
                        it.copy(
                            helperTasks = tasks.sortedByDescending { task -> task.updatedAt },
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onTabSelected(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
}
