package com.ryuzora.dangani.presentation.viewmodel.submission

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
import com.ryuzora.dangani.domain.usecase.profile.GetUserProfileUseCase
import com.ryuzora.dangani.domain.usecase.submission.SubmitWorkUseCase
import com.ryuzora.dangani.domain.usecase.task.GetTaskByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.ryuzora.dangani.data.repository.ReviewRepositoryImpl
import com.ryuzora.dangani.domain.model.Review
import com.ryuzora.dangani.domain.usecase.review.GetReviewByTaskIdUseCase

data class WorkSubmissionUiState(
    val task: Task? = null,
    val requester: User? = null,
    val selectedFileUri: String? = null,
    val selectedFileName: String? = null,
    val review: Review? = null,
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
    private val userRepo = UserRepositoryImpl(db.userDao(), firebaseAuth, firestoreService, storageService)

    private val reviewRepo = ReviewRepositoryImpl(firestoreService)

    private val getTaskByIdUseCase = GetTaskByIdUseCase(taskRepo)
    private val getUserProfileUseCase = GetUserProfileUseCase(userRepo)
    private val submitWorkUseCase = SubmitWorkUseCase(taskRepo)

    private val getReviewByTaskIdUseCase = GetReviewByTaskIdUseCase(reviewRepo)

    private val _uiState = MutableStateFlow(WorkSubmissionUiState())
    val uiState: StateFlow<WorkSubmissionUiState> = _uiState.asStateFlow()

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getTaskByIdUseCase(taskId).collect { task ->
                _uiState.update {
                    it.copy(
                        task = task,
                        isLoading = false
                    )
                }

                if (task != null) {
                    launch {
                        getUserProfileUseCase(task.requesterId).collect { requester ->
                            _uiState.update {
                                it.copy(requester = requester)
                            }
                        }
                    }

                    launch {
                        getReviewByTaskIdUseCase(task.id).collect { review ->
                            _uiState.update {
                                it.copy(review = review)
                            }
                        }
                    }
                }
            }
        }
    }

    fun onFileSelected(uri: String, fileName: String) {
        _uiState.update { it.copy(selectedFileUri = uri, selectedFileName = fileName, error = null) }
    }

    fun submitWork() {
        val fileUri = _uiState.value.selectedFileUri
        if (fileUri.isNullOrBlank()) {
            _uiState.update { it.copy(error = "Pilih file bukti pengerjaan terlebih dahulu") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true, error = null) }
            submitWorkUseCase(taskId, fileUri)
                .onSuccess {
                    _uiState.update { it.copy(isUploading = false, isSubmitted = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isUploading = false, error = e.message ?: "Gagal mengirim pekerjaan") }
                }
        }
    }

    fun cancelTask() {
        // TODO: Implement cancel task logic
    }
}


