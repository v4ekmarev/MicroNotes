package com.develop.feature.splash.data.repository

import com.develop.core.common.AppLogger
import com.develop.feature.splash.data.api.AuthApi

import com.develop.data.network.api.DeviceIdProvider
import com.develop.data.network.api.TokenProvider
import com.develop.feature.splash.domain.AuthRepository
import com.develop.feature.splash.domain.model.DeviceAuthResponse

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenProvider: TokenProvider,
    private val deviceIdProvider: DeviceIdProvider
) : AuthRepository {
    
    override suspend fun authenticate(): Result<DeviceAuthResponse> {
        val existingDeviceId = deviceIdProvider.getDeviceId()
        
        return authApi.authenticateDevice(existingDeviceId)
            .onSuccess { response ->
                tokenProvider.saveToken(response.token)
                if (response.isNewUser) {
                    deviceIdProvider.saveDeviceId(response.deviceId)
                }
            }
            .onFailure {
                AppLogger.d("ASDASD",it.toString())
            }
    }
    
    override suspend fun logout() {
        tokenProvider.clearToken()
    }
    
    override suspend fun isLoggedIn(): Boolean {
        return tokenProvider.getToken() != null
    }
}
