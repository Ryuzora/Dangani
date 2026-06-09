package com.ryuzora.dangani.domain.usecase.auth

import com.ryuzora.dangani.domain.model.User
import com.ryuzora.dangani.domain.repository.UserRepository

class RegisterUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): Result<User> {
        if (!email.endsWith("@mhs.ulm.ac.id")) {
            return Result.failure(IllegalArgumentException("Email harus menggunakan @mhs.ulm.ac.id"))
        }
        if (username.isBlank()) {
            return Result.failure(IllegalArgumentException("Username tidak boleh kosong"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password minimal 6 karakter"))
        }
        if (password != confirmPassword) {
            return Result.failure(IllegalArgumentException("Password tidak cocok"))
        }
        return userRepository.register(email, username, password)
    }
}
