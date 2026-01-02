package com.develop.feature.splash.domain

import com.develop.feature.splash.domain.model.DeviceAuthResponse

interface AuthRepository {
    suspend fun authenticate(): Result<DeviceAuthResponse>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}
