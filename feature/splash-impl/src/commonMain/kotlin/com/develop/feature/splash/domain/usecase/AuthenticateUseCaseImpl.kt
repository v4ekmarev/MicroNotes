package com.develop.feature.splash.domain.usecase

import com.develop.feature.splash.domain.AuthRepository
import com.develop.feature.splash.domain.model.DeviceAuthResponse

class AuthenticateUseCaseImpl(
    private val repository: AuthRepository
) : AuthenticateUseCase {
    
    override suspend fun execute(): Result<DeviceAuthResponse> {
        return repository.authenticate()
    }
}
