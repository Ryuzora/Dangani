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
import com.ryuzora.dangani.domain.model.TaskCategory
import com.ryuzora.dangani.domain.usecase.task.CreateTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val selectedCategory: TaskCategory? = null,
    val selectedPoints: Int = 0,
    val isCategoryDropdownExpanded: Boolean = false,
    val isPublishing: Boolean = false,
    val titleError: String? = null,
    val descriptionError: String? = null,
    val error: String? = null,
    val isCreated: Boolean = false
)

class CreateTaskViewModel : ViewModel() {

    private val db = DanganiApplication.instance.database
    private val firebaseAuth = FirebaseAuthService()
    private val firestoreService = FirestoreService()
    private val storageService = FirebaseStorageService()

    private val notificationRepo = NotificationRepositoryImpl(db.notificationDao(), firestoreService)
    private val taskRepo = TaskRepositoryImpl(db.taskDao(), db.taskApplicationDao(), firestoreService, storageService, notificationRepo)
    private val userRepo = UserRepositoryImpl(db.userDao(), firebaseAuth, firestoreService, storageService)

    private val createTaskUseCase = CreateTaskUseCase(taskRepo, userRepo)

    private val _uiState = MutableStateFlow(CreateTaskUiState())
    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title, titleError = null, error = null) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description, descriptionError = null, error = null) }
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

    fun publishTask() {
        val state = _uiState.value
        var hasError = false

        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Judul tugas tidak boleh kosong") }
            hasError = true
        }
        if (state.description.isBlank()) {
            _uiState.update { it.copy(descriptionError = "Deskripsi tugas tidak boleh kosong") }
            hasError = true
        }
        if (state.selectedCategory == null) {
            _uiState.update { it.copy(error = "Pilih kategori tugas") }
            hasError = true
        }
        if (state.selectedPoints == 0) {
            _uiState.update { it.copy(error = "Pilih poin tugas") }
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isPublishing = true, error = null) }
            createTaskUseCase(
                title = state.title.trim(),
                description = state.description.trim(),
                category = state.selectedCategory!!,
                points = state.selectedPoints
            )
                .onSuccess {
                    _uiState.update { it.copy(isPublishing = false, isCreated = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isPublishing = false, error = e.message ?: "Gagal membuat tugas") }
                }
        }
    }
}
