package com.develop.feature.profile.domain.usecase

import com.develop.feature.profile.domain.ProfileRepository
import com.develop.feature.profile.domain.model.UserProfile

class UpdateProfileUseCaseImpl(
    private val repository: ProfileRepository
) : UpdateProfileUseCase {
    
    override suspend fun execute(username: String?, phone: String?): Result<UserProfile> {
        return repository.updateProfile(username, phone)
    }
}
