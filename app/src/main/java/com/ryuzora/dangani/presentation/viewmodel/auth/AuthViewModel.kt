package com.ryuzora.dangani.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryuzora.dangani.domain.model.User
import com.ryuzora.dangani.domain.usecase.auth.LoginUseCase
import com.ryuzora.dangani.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val generalError: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, generalError = null) }
    }

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, usernameError = null, generalError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, generalError = null) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null, generalError = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun login() {
        val state = _uiState.value
        // Client-side validation
        var hasError = false
        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email tidak boleh kosong") }
            hasError = true
        } else if (!state.email.endsWith("@mhs.ulm.ac.id")) {
            _uiState.update { it.copy(emailError = "Gunakan email @mhs.ulm.ac.id") }
            hasError = true
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Password tidak boleh kosong") }
            hasError = true
        } else if (state.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Password minimal 6 karakter") }
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            loginUseCase(state.email.trim(), state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, generalError = e.message ?: "Login gagal") }
                }
        }
    }

    fun register() {
        val state = _uiState.value
        var hasError = false

        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email tidak boleh kosong") }
            hasError = true
        } else if (!state.email.endsWith("@mhs.ulm.ac.id")) {
            _uiState.update { it.copy(emailError = "Gunakan email @mhs.ulm.ac.id") }
            hasError = true
        }
        if (state.username.isBlank()) {
            _uiState.update { it.copy(usernameError = "Username tidak boleh kosong") }
            hasError = true
        }
        if (state.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Password tidak boleh kosong") }
            hasError = true
        } else if (state.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Password minimal 6 karakter") }
            hasError = true
        }
        if (state.confirmPassword != state.password) {
            _uiState.update { it.copy(confirmPasswordError = "Password tidak cocok") }
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }
            registerUseCase(state.email.trim(), state.username.trim(), state.password, state.confirmPassword)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, generalError = e.message ?: "Registrasi gagal") }
                }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }
}


