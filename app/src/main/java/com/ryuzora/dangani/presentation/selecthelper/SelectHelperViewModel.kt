package com.ryuzora.dangani.presentation.selecthelper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryuzora.dangani.DanganiApplication
import com.ryuzora.dangani.data.remote.FirebaseAuthService
import com.ryuzora.dangani.data.remote.FirebaseStorageService
import com.ryuzora.dangani.data.remote.FirestoreService
import com.ryuzora.dangani.data.repository.NotificationRepositoryImpl
import com.ryuzora.dangani.data.repository.TaskRepositoryImpl
import com.ryuzora.dangani.domain.model.Task
import com.ryuzora.dangani.domain.model.TaskApplication
import com.ryuzora.dangani.domain.usecase.application.AcceptHelperUseCase
import com.ryuzora.dangani.domain.usecase.application.GetApplicantsUseCase
import com.ryuzora.dangani.domain.usecase.task.GetTaskByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SelectHelperUiState(
    val task: Task? = null,
    val applicants: List<TaskApplication> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val acceptedHelperId: String? = null
)

class SelectHelperViewModel(private val taskId: String) : ViewModel() {

    private val db = DanganiApplication.instance.database
    private val firebaseAuth = FirebaseAuthService()
    private val firestoreService = FirestoreService()
    private val storageService = FirebaseStorageService()

    private val notificationRepo = NotificationRepositoryImpl(db.notificationDao(), firestoreService)
    private val taskRepo = TaskRepositoryImpl(db.taskDao(), db.taskApplicationDao(), firestoreService, storageService, notificationRepo)

    private val getTaskByIdUseCase = GetTaskByIdUseCase(taskRepo)
    private val getApplicantsUseCase = GetApplicantsUseCase(taskRepo)
    private val acceptHelperUseCase = AcceptHelperUseCase(taskRepo)

    private val _uiState = MutableStateFlow(SelectHelperUiState())
    val uiState: StateFlow<SelectHelperUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Load task
            launch {
                getTaskByIdUseCase(taskId).collect { task ->
                    _uiState.update { it.copy(task = task, isLoading = false) }
                }
            }

            // Load applicants
            launch {
                getApplicantsUseCase(taskId).collect { applicants ->
                    _uiState.update { it.copy(applicants = applicants, isLoading = false) }
                }
            }
        }
    }

    fun acceptHelper(helperId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            acceptHelperUseCase(taskId, helperId)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, acceptedHelperId = helperId) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Gagal menerima helper") }
                }
        }
    }
}
