package com.ryuzora.dangani.presentation.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryuzora.dangani.DanganiApplication
import com.ryuzora.dangani.data.remote.FirebaseAuthService
import com.ryuzora.dangani.data.remote.FirebaseStorageService
import com.ryuzora.dangani.data.remote.FirestoreService
import com.ryuzora.dangani.data.remote.dto.ReviewDto
import com.ryuzora.dangani.data.remote.dto.TaskDto
import com.ryuzora.dangani.data.repository.SearchRepositoryImpl
import com.ryuzora.dangani.data.repository.UserRepositoryImpl
import com.ryuzora.dangani.domain.model.Review
import com.ryuzora.dangani.domain.model.TaskStatus
import com.ryuzora.dangani.domain.model.User
import com.ryuzora.dangani.domain.usecase.auth.LogoutUseCase
import com.ryuzora.dangani.domain.usecase.profile.GetUserProfileUseCase
import com.ryuzora.dangani.domain.usecase.profile.GetUserReviewsUseCase
import com.ryuzora.dangani.domain.usecase.profile.UploadProfilePhotoUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: User? = null,
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = true,
    val isUploadingPhoto: Boolean = false,
    val isLoggedOut: Boolean = false,
    val error: String? = null
)

class ProfileViewModel(
    private val targetUserId: String? = null
) : ViewModel() {

    private val db = DanganiApplication.instance.database
    private val firebaseAuth = FirebaseAuthService()
    private val firestoreService = FirestoreService()
    private val storageService = FirebaseStorageService()

    private val userRepo = UserRepositoryImpl(db.userDao(), firebaseAuth, firestoreService, storageService)
    private val searchRepo = SearchRepositoryImpl(db.searchHistoryDao(), db.favoriteDao(), db.reviewDao(), firestoreService)

    private val getUserProfileUseCase = GetUserProfileUseCase(userRepo)
    private val getUserReviewsUseCase = GetUserReviewsUseCase(searchRepo)
    private val uploadProfilePhotoUseCase = UploadProfilePhotoUseCase(userRepo)
    private val updateProfileUseCase = com.ryuzora.dangani.domain.usecase.profile.UpdateProfileUseCase(userRepo)
    private val logoutUseCase = LogoutUseCase(userRepo)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val currentUserId: String? = userRepo.getCurrentUserId()
    val isOwnProfile: Boolean = targetUserId == null || targetUserId == currentUserId

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val userId = targetUserId ?: currentUserId ?: return

        // Load user profile
        viewModelScope.launch {
            var didRefreshStats = false
            getUserProfileUseCase(userId)
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { user ->
                    _uiState.update { it.copy(user = user, isLoading = false) }
                    if (user != null && !didRefreshStats) {
                        didRefreshStats = true
                        refreshProfileStats(userId)
                    }
                }
        }

        // Load reviews
        viewModelScope.launch {
            getUserReviewsUseCase(userId)
                .catch { /* silently handle */ }
                .collect { reviews ->
                    _uiState.update {
                        it.copy(reviews = reviews.latestReviewPerTask())
                    }
                }
        }
    }

    private suspend fun refreshProfileStats(userId: String) {
        try {
            val requesterTasks = firestoreService
                .queryCollection("tasks", "requesterId", userId)
                .first()
                .mapNotNull { it.toObject(TaskDto::class.java) }

            val completedHelperTasks = firestoreService
                .queryCollection("tasks", "helperId", userId)
                .first()
                .mapNotNull { it.toObject(TaskDto::class.java) }
                .filter { it.status == TaskStatus.ACCEPTED.name }

            val reviews = firestoreService
                .queryCollection("reviews", "revieweeId", userId)
                .first()
                .mapNotNull { it.toObject(ReviewDto::class.java) }
                .latestReviewDtoPerTask()

            val stats = mapOf(
                "tasksUploaded" to requesterTasks.size,
                "averageTaskPoints" to if (requesterTasks.isNotEmpty()) requesterTasks.map { it.taskPoints }.average() else 0.0,
                "tasksCompleted" to completedHelperTasks.size,
                "totalPoints" to completedHelperTasks.sumOf { it.taskPoints },
                "ratingAverage" to if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else 0.0
            )

            firestoreService.updateDocument("users", userId, stats)

            _uiState.update { state ->
                state.copy(
                    user = state.user?.copy(
                        tasksUploaded = stats["tasksUploaded"] as Int,
                        averageTaskPoints = stats["averageTaskPoints"] as Double,
                        tasksCompleted = stats["tasksCompleted"] as Int,
                        totalPoints = stats["totalPoints"] as Int,
                        ratingAverage = stats["ratingAverage"] as Double
                    )
                )
            }
        } catch (_: Exception) { }
    }

    private fun List<Review>.latestReviewPerTask(): List<Review> {
        return groupBy { it.taskId.ifBlank { it.id } }
            .values
            .mapNotNull { reviews -> reviews.maxByOrNull { it.createdAt } }
            .sortedByDescending { it.createdAt }
    }

    private fun List<ReviewDto>.latestReviewDtoPerTask(): List<ReviewDto> {
        return groupBy { it.taskId.ifBlank { it.id } }
            .values
            .mapNotNull { reviews -> reviews.maxByOrNull { it.createdAt } }
    }

    fun uploadPhoto(imageUri: String) {
        val userId = currentUserId ?: return
        if (!isOwnProfile) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUploadingPhoto = true, error = null) }
            uploadProfilePhotoUseCase(userId, imageUri)
                .onSuccess {
                    _uiState.update { it.copy(isUploadingPhoto = false) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isUploadingPhoto = false,
                            error = e.message ?: "Gagal mengunggah foto"
                        )
                    }
                }
        }
    }

    fun updateProfile(username: String, whatsapp: String, instagram: String) {
        val currentUser = _uiState.value.user ?: return
        if (!isOwnProfile) return

        val formattedWhatsapp = if (whatsapp.startsWith("0")) {
            "62" + whatsapp.substring(1)
        } else if (whatsapp.startsWith("+62")) {
            whatsapp.substring(1)
        } else {
            whatsapp
        }

        viewModelScope.launch {
            val updatedUser = currentUser.copy(
                username = username,
                whatsapp = formattedWhatsapp,
                instagram = instagram
            )
            updateProfileUseCase(updatedUser)
                .onSuccess {
                    // Update local state optimistic
                    _uiState.update { it.copy(user = updatedUser) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message ?: "Gagal menyimpan profil") }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}


