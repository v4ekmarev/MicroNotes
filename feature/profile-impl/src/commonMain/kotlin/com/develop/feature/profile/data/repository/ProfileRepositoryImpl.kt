package com.develop.feature.profile.data.repository

import com.develop.feature.profile.data.api.ProfileApi
import com.develop.feature.profile.domain.ProfileRepository
import com.develop.feature.profile.domain.model.UserProfile

class ProfileRepositoryImpl(
    private val profileApi: ProfileApi
) : ProfileRepository {
    
    override suspend fun getProfile(): Result<UserProfile> {
        return profileApi.getProfile().map { dto ->
            UserProfile(
                id = dto.id,
                username = dto.username,
                phone = dto.phone
            )
        }
    }
    
    override suspend fun updateProfile(username: String?, phone: String?): Result<UserProfile> {
        return profileApi.updateProfile(username, phone).map { dto ->
            UserProfile(
                id = dto.id,
                username = dto.username,
                phone = dto.phone
            )
        }
    }
}
