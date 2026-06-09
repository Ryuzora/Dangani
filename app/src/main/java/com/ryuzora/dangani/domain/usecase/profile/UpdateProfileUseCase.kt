package com.ryuzora.dangani.domain.usecase.profile

import com.ryuzora.dangani.domain.model.User
import com.ryuzora.dangani.domain.repository.UserRepository

class UpdateProfileUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User): Result<Unit> {
        if (user.username.isBlank()) {
            return Result.failure(IllegalArgumentException("Username tidak boleh kosong"))
        }
        return userRepository.updateProfile(user)
    }
}
