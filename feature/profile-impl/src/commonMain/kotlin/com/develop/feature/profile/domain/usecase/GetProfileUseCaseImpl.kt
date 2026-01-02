package com.develop.feature.profile.domain.usecase

import com.develop.feature.profile.domain.ProfileRepository
import com.develop.feature.profile.domain.model.UserProfile

class GetProfileUseCaseImpl(
    private val repository: ProfileRepository
) : GetProfileUseCase {
    
    override suspend fun execute(): Result<UserProfile> {
        return repository.getProfile()
    }
}
