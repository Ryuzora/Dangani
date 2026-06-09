package com.ryuzora.dangani.domain.usecase.auth

import com.ryuzora.dangani.domain.repository.UserRepository

class LogoutUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke() {
        userRepository.logout()
    }
}
