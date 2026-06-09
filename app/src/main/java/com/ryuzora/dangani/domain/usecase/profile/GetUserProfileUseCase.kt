package com.ryuzora.dangani.domain.usecase.profile

import com.ryuzora.dangani.domain.model.User
import com.ryuzora.dangani.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUserProfileUseCase(private val userRepository: UserRepository) {
    operator fun invoke(userId: String): Flow<User?> {
        return userRepository.getUserProfile(userId)
    }
}
