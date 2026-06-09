package com.ryuzora.dangani.presentation.submission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryuzora.dangani.DanganiApplication
import com.ryuzora.dangani.data.remote.FirebaseAuthService
import com.ryuzora.dangani.data.remote.FirebaseStorageService
import com.ryuzora.dangani.data.remote.FirestoreService
import com.ryuzora.dangani.data.repository.NotificationRepositoryImpl
import com.ryuzora.dangani.data.repository.TaskRepositoryImpl
import com.ryuzora.dangani.domain.model.Task
import com.ryuzora.dangani.domain.usecase.submission.SubmitWorkUseCase
import com.ryuzora.dangani.domain.usecase.task.GetTaskByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WorkSubmissionUiState(
    val task: Task? = null,
    val driveLink: String = "",
    val isUploading: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSubmitted: Boolean = false
)

class WorkSubmissionViewModel(private val taskId: String) : ViewModel() {

    private val db = DanganiApplication.instance.database
    private val firebaseAuth = FirebaseAuthService()
    private val firestoreService = FirestoreService()
    private val storageService = FirebaseStorageService()

    private val notificationRepo = NotificationRepositoryImpl(db.notificationDao(), firestoreService)
    private val taskRepo = TaskRepositoryImpl(db.taskDao(), db.taskApplicationDao(), firestoreService, storageService, notificationRepo)

    private val getTaskByIdUseCase = GetTaskByIdUseCase(taskRepo)
    private val submitWorkUseCase = SubmitWorkUseCase(taskRepo)

    private val _uiState = MutableStateFlow(WorkSubmissionUiState())
    val uiState: StateFlow<WorkSubmissionUiState> = _uiState.asStateFlow()

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getTaskByIdUseCase(taskId).collect { task ->
                _uiState.update { it.copy(task = task, isLoading = false) }
            }
        }
    }

    fun onDriveLinkChanged(link: String) {
        _uiState.update { it.copy(driveLink = link, error = null) }
    }

    fun submitWork() {
        val link = _uiState.value.driveLink.trim()
        if (link.isBlank()) {
            _uiState.update { it.copy(error = "Masukkan link Google Drive terlebih dahulu") }
            return
        }

        if (!isValidDriveLink(link)) {
            _uiState.update { it.copy(error = "Masukkan link Google Drive yang valid") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true, error = null) }
            submitWorkUseCase(taskId, link)
                .onSuccess {
                    _uiState.update { it.copy(isUploading = false, isSubmitted = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isUploading = false, error = e.message ?: "Gagal mengirim pekerjaan") }
                }
        }
    }

    private fun isValidDriveLink(link: String): Boolean {
        return link.startsWith("https://drive.google.com") ||
                link.startsWith("https://docs.google.com") ||
                link.startsWith("http://drive.google.com") ||
                link.startsWith("http://docs.google.com")
    }

    fun cancelTask() {
        // TODO: Implement cancel task logic
    }
}
