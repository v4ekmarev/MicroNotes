package com.develop.feature.profile.domain.usecase

import com.develop.feature.profile.domain.model.UserProfile

interface UpdateProfileUseCase {
    suspend fun execute(username: String?, phone: String?): Result<UserProfile>
}
