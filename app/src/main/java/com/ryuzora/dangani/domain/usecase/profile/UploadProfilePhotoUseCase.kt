package com.ryuzora.dangani.domain.usecase.profile

import com.ryuzora.dangani.domain.repository.UserRepository

class UploadProfilePhotoUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String, imageUri: String): Result<String> {
        return userRepository.uploadProfilePhoto(userId, imageUri)
    }
}
