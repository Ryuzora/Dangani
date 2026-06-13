package com.ryuzora.dangani.domain.usecase.auth

import com.ryuzora.dangani.domain.model.User
import com.ryuzora.dangani.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentUserUseCase(private val userRepository: UserRepository) {
    operator fun invoke(): Flow<User?> {
        return userRepository.getCurrentUserProfile()
    }
}

