package com.ryuzora.dangani.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryuzora.dangani.DanganiApplication
import com.ryuzora.dangani.data.remote.FirebaseAuthService
import com.ryuzora.dangani.data.remote.FirebaseStorageService
import com.ryuzora.dangani.data.remote.FirestoreService
import com.ryuzora.dangani.data.repository.NotificationRepositoryImpl
import com.ryuzora.dangani.data.repository.UserRepositoryImpl
import com.ryuzora.dangani.domain.model.Notification
import com.ryuzora.dangani.domain.usecase.notification.GetNotificationsUseCase
import com.ryuzora.dangani.domain.usecase.notification.MarkNotificationReadUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationUiState(
    val requesterNotifications: List<Notification> = emptyList(),
    val helperNotifications: List<Notification> = emptyList(),
    val selectedTab: Int = 0, // 0 = requester, 1 = helper
    val requesterUnreadCount: Int = 0,
    val helperUnreadCount: Int = 0,
    val isLoading: Boolean = true
)

class NotificationViewModel : ViewModel() {

    private val db = DanganiApplication.instance.database
    private val firebaseAuth = FirebaseAuthService()
    private val firestoreService = FirestoreService()
    private val storageService = FirebaseStorageService()

    private val userRepo = UserRepositoryImpl(db.userDao(), firebaseAuth, firestoreService, storageService)
    private val notificationRepo = NotificationRepositoryImpl(db.notificationDao(), firestoreService)

    private val getNotificationsUseCase = GetNotificationsUseCase(notificationRepo)
    private val markNotificationReadUseCase = MarkNotificationReadUseCase(notificationRepo)

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        val userId = userRepo.getCurrentUserId()
        if (userId == null) {
            _uiState.update { it.copy(isLoading = false) }
            return
        }

        // Load requester notifications
        viewModelScope.launch {
            getNotificationsUseCase(userId, "requester")
                .catch { /* silently handle */ }
                .collect { notifications ->
                    _uiState.update {
                        it.copy(
                            requesterNotifications = notifications.sortedByDescending { n -> n.createdAt },
                            isLoading = false
                        )
                    }
                }
        }

        // Load helper notifications
        viewModelScope.launch {
            getNotificationsUseCase(userId, "helper")
                .catch { /* silently handle */ }
                .collect { notifications ->
                    _uiState.update {
                        it.copy(
                            helperNotifications = notifications.sortedByDescending { n -> n.createdAt },
                            isLoading = false
                        )
                    }
                }
        }

        // Load unread counts
        viewModelScope.launch {
            notificationRepo.getUnreadCount(userId, "requester")
                .catch { /* silently handle */ }
                .collect { count ->
                    _uiState.update { it.copy(requesterUnreadCount = count) }
                }
        }

        viewModelScope.launch {
            notificationRepo.getUnreadCount(userId, "helper")
                .catch { /* silently handle */ }
                .collect { count ->
                    _uiState.update { it.copy(helperUnreadCount = count) }
                }
        }
    }

    fun onTabSelected(tab: Int) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onNotificationClick(notification: Notification) {
        if (!notification.isRead) {
            viewModelScope.launch {
                markNotificationReadUseCase(notification.id)
            }
        }
    }
}
