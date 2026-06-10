package com.ryuzora.dangani.presentation.task

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
import com.ryuzora.dangani.domain.model.TaskCategory
import com.ryuzora.dangani.domain.model.TaskStatus
import com.ryuzora.dangani.domain.model.User
import com.ryuzora.dangani.domain.usecase.profile.GetUserProfileUseCase
import com.ryuzora.dangani.domain.usecase.submission.AcceptWorkUseCase
import com.ryuzora.dangani.domain.usecase.submission.RequestRevisionUseCase
import com.ryuzora.dangani.domain.usecase.task.DeleteTaskUseCase
import com.ryuzora.dangani.domain.usecase.task.GetTaskByIdUseCase
import com.ryuzora.dangani.domain.usecase.task.UpdateTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditTaskUiState(
    val task: Task? = null,
    val helper: User? = null,
    val title: String = "",
    val description: String = "",
    val selectedCategory: TaskCategory? = null,
    val selectedPoints: Int = 0,
    val isCategoryDropdownExpanded: Boolean = false,
    val isEditable: Boolean = true,
    val proofSubmitted: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val error: String? = null,
    val isDeleted: Boolean = false,
    val isSaved: Boolean = false
)

class EditTaskViewModel(private val taskId: String) : ViewModel() {

    private val db = DanganiApplication.instance.database
    private val firebaseAuth = FirebaseAuthService()
    private val firestoreService = FirestoreService()
    private val storageService = FirebaseStorageService()

    private val notificationRepo = NotificationRepositoryImpl(db.notificationDao(), firestoreService)
    private val taskRepo = TaskRepositoryImpl(db.taskDao(), db.taskApplicationDao(), firestoreService, storageService, notificationRepo)
    private val userRepo = UserRepositoryImpl(db.userDao(), firebaseAuth, firestoreService, storageService)

    private val getTaskByIdUseCase = GetTaskByIdUseCase(taskRepo)
    private val updateTaskUseCase = UpdateTaskUseCase(taskRepo)
    private val deleteTaskUseCase = DeleteTaskUseCase(taskRepo)
    private val getUserProfileUseCase = GetUserProfileUseCase(userRepo)
    private val acceptWorkUseCase = AcceptWorkUseCase(taskRepo)
    private val requestRevisionUseCase = RequestRevisionUseCase(taskRepo)

    private val _uiState = MutableStateFlow(EditTaskUiState())
    val uiState: StateFlow<EditTaskUiState> = _uiState.asStateFlow()

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getTaskByIdUseCase(taskId).collect { task ->
                if (task != null) {
                    val proofSubmitted = task.proofOfWorkUrl.isNotBlank()
                    _uiState.update {
                        it.copy(
                            task = task,
                            title = task.title,
                            description = task.description,
                            selectedCategory = task.category,
                            selectedPoints = task.taskPoints,
                            isEditable = task.isEditable,
                            proofSubmitted = proofSubmitted,
                            isLoading = false
                        )
                    }

                    // Load helper profile if assigned
                    if (task.helperId.isNotBlank()) {
                        launch {
                            getUserProfileUseCase(task.helperId).collect { user ->
                                _uiState.update { it.copy(helper = user) }
                            }
                        }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Tugas tidak ditemukan") }
                }
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title, error = null) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description, error = null) }
    }

    fun onCategorySelected(category: TaskCategory) {
        _uiState.update { it.copy(selectedCategory = category, isCategoryDropdownExpanded = false, error = null) }
    }

    fun onPointsSelected(points: Int) {
        _uiState.update { it.copy(selectedPoints = points, error = null) }
    }

    fun onCategoryDropdownToggle() {
        _uiState.update { it.copy(isCategoryDropdownExpanded = !it.isCategoryDropdownExpanded) }
    }

    fun onCategoryDropdownDismiss() {
        _uiState.update { it.copy(isCategoryDropdownExpanded = false) }
    }

    fun saveTask() {
        val state = _uiState.value
        val task = state.task ?: return

        if (state.title.isBlank() || state.description.isBlank()) {
            _uiState.update { it.copy(error = "Judul dan deskripsi tidak boleh kosong") }
            return
        }

        val updatedTask = task.copy(
            title = state.title.trim(),
            description = state.description.trim(),
            category = state.selectedCategory ?: task.category,
            taskPoints = state.selectedPoints
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            updateTaskUseCase(updatedTask)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, error = e.message ?: "Gagal menyimpan tugas") }
                }
        }
    }

    fun deleteTask() {
        val task = _uiState.value.task ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, error = null) }
            deleteTaskUseCase(task)
                .onSuccess {
                    _uiState.update { it.copy(isDeleting = false, isDeleted = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isDeleting = false, error = e.message ?: "Gagal menghapus tugas") }
                }
        }
    }

    fun acceptWork() {
        val task = _uiState.value.task ?: return

        if (task.status != TaskStatus.NEED_REVIEW) {
            _uiState.update {
                it.copy(error = "Tugas ini tidak bisa diterima karena statusnya bukan Need Review")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            acceptWorkUseCase(taskId)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            error = e.message ?: "Gagal menerima pekerjaan"
                        )
                    }
                }
        }
    }

    fun requestRevision() {
        val task = _uiState.value.task ?: return

        if (task.status != TaskStatus.NEED_REVIEW) {
            _uiState.update {
                it.copy(error = "Tugas ini tidak bisa diminta revisi karena statusnya bukan Need Review")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            requestRevisionUseCase(taskId)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            error = e.message ?: "Gagal meminta revisi"
                        )
                    }
                }
        }
    }
}
