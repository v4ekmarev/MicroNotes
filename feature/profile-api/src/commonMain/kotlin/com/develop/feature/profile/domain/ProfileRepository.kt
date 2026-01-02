package com.develop.feature.profile.domain

import com.develop.feature.profile.domain.model.UserProfile

interface ProfileRepository {
    suspend fun getProfile(): Result<UserProfile>
    suspend fun updateProfile(username: String?, phone: String?): Result<UserProfile>
}
