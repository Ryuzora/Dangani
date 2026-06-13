package com.ryuzora.dangani.domain.usecase.auth

import com.ryuzora.dangani.domain.model.User
import com.ryuzora.dangani.domain.repository.UserRepository

class LoginUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (!email.endsWith("@mhs.ulm.ac.id")) {
            return Result.failure(IllegalArgumentException("Email harus menggunakan @mhs.ulm.ac.id"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password minimal 6 karakter"))
        }
        return userRepository.login(email, password)
    }
}

