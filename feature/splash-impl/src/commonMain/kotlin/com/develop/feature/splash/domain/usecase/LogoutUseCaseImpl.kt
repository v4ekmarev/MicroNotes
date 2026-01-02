package com.develop.feature.splash.domain.usecase

import com.develop.feature.splash.domain.AuthRepository

class LogoutUseCaseImpl(
    private val repository: AuthRepository
) : LogoutUseCase {
    
    override suspend fun execute() {
        repository.logout()
    }
}
