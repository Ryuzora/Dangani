package com.ryuzora.dangani.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryuzora.dangani.DanganiApplication
import com.ryuzora.dangani.data.remote.FirebaseAuthService
import com.ryuzora.dangani.data.remote.FirebaseStorageService
import com.ryuzora.dangani.data.remote.FirestoreService
import com.ryuzora.dangani.data.repository.SearchRepositoryImpl
import com.ryuzora.dangani.data.repository.UserRepositoryImpl
import com.ryuzora.dangani.domain.model.Review
import com.ryuzora.dangani.domain.model.User
import com.ryuzora.dangani.domain.usecase.auth.LogoutUseCase
import com.ryuzora.dangani.domain.usecase.profile.GetUserProfileUseCase
import com.ryuzora.dangani.domain.usecase.profile.GetUserReviewsUseCase
import com.ryuzora.dangani.domain.usecase.profile.UploadProfilePhotoUseCase
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
            getUserProfileUseCase(userId)
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { user ->
                    _uiState.update { it.copy(user = user, isLoading = false) }
                }
        }

        // Load reviews
        viewModelScope.launch {
            getUserReviewsUseCase(userId)
                .catch { /* silently handle */ }
                .collect { reviews ->
                    _uiState.update {
                        it.copy(reviews = reviews.sortedByDescending { r -> r.createdAt })
                    }
                }
        }
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
