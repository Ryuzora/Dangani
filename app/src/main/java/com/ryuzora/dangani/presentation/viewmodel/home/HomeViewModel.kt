package com.ryuzora.dangani.presentation.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryuzora.dangani.DanganiApplication
import com.ryuzora.dangani.data.remote.FirebaseAuthService
import com.ryuzora.dangani.data.remote.FirebaseStorageService
import com.ryuzora.dangani.data.remote.FirestoreService
import com.ryuzora.dangani.data.repository.NotificationRepositoryImpl
import com.ryuzora.dangani.data.repository.SearchRepositoryImpl
import com.ryuzora.dangani.data.repository.TaskRepositoryImpl
import com.ryuzora.dangani.domain.model.Task
import com.ryuzora.dangani.domain.model.TaskCategory
import com.ryuzora.dangani.domain.usecase.search.GetSearchHistoryUseCase
import com.ryuzora.dangani.domain.usecase.task.FilterTasksByCategoryUseCase
import com.ryuzora.dangani.domain.usecase.task.GetAllTasksUseCase
import com.ryuzora.dangani.domain.usecase.task.SearchTasksUseCase
import com.ryuzora.dangani.domain.model.TaskStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val tasks: List<Task> = emptyList(),
    val categories: List<TaskCategory> = TaskCategory.getCategoriesWithAll(),
    val selectedCategory: TaskCategory? = null,
    val searchQuery: String = "",
    val searchHistory: List<String> = emptyList(),
    val isSearchActive: Boolean = false,
    val isLoading: Boolean = true
)

class HomeViewModel : ViewModel() {

    private val db = DanganiApplication.instance.database
    private val firebaseAuth = FirebaseAuthService()
    private val firestoreService = FirestoreService()
    private val storageService = FirebaseStorageService()

    private val notificationRepo = NotificationRepositoryImpl(db.notificationDao(), firestoreService)
    private val taskRepo = TaskRepositoryImpl(db.taskDao(), db.taskApplicationDao(), firestoreService, storageService, notificationRepo)
    private val searchRepo = SearchRepositoryImpl(db.searchHistoryDao(), db.favoriteDao(), db.reviewDao(), firestoreService)

    private val getAllTasksUseCase = GetAllTasksUseCase(taskRepo)
    private val filterTasksByCategoryUseCase = FilterTasksByCategoryUseCase(taskRepo)
    private val searchTasksUseCase = SearchTasksUseCase(taskRepo, searchRepo)
    private val getSearchHistoryUseCase = GetSearchHistoryUseCase(searchRepo)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var tasksJob: Job? = null
    private var searchHistoryJob: Job? = null

    init {
        loadTasks()
        loadSearchHistory()
    }

    private fun onlyAvailableTasks(tasks: List<Task>): List<Task> {
        return tasks.filter { task ->
            task.status == TaskStatus.UNASSIGNED && task.helperId.isBlank()
        }
    }

    private fun loadTasks() {
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getAllTasksUseCase().collect { tasks ->
                _uiState.update {
                    it.copy(
                        tasks = onlyAvailableTasks(tasks),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadSearchHistory() {
        val userId = firebaseAuth.getCurrentUserId() ?: return
        searchHistoryJob?.cancel()
        searchHistoryJob = viewModelScope.launch {
            getSearchHistoryUseCase(userId).collect { history ->
                _uiState.update { it.copy(searchHistory = history) }
            }
        }
    }

    fun onCategorySelected(category: TaskCategory) {
        _uiState.update { it.copy(selectedCategory = category, searchQuery = "") }
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            filterTasksByCategoryUseCase(category).collect { tasks ->
                _uiState.update {
                    it.copy(
                        tasks = onlyAvailableTasks(tasks),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onSearch(query: String) {
        if (query.isBlank()) {
            loadTasks()
            return
        }
        val userId = firebaseAuth.getCurrentUserId() ?: return
        _uiState.update { it.copy(selectedCategory = null, isSearchActive = false) }
        tasksJob?.cancel()
        tasksJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            searchTasksUseCase(userId, query).collect { tasks ->
                _uiState.update {
                    it.copy(
                        tasks = onlyAvailableTasks(tasks),
                        isLoading = false
                    )
                }
            }
        }
        loadSearchHistory()
    }

    fun onSearchHistoryItemClick(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        onSearch(query)
    }

    fun onSearchActiveChange(isActive: Boolean) {
        _uiState.update { it.copy(isSearchActive = isActive) }
    }
}


