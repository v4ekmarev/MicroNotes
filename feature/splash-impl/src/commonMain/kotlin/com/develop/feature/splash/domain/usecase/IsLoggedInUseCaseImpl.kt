package com.develop.feature.splash.domain.usecase

import com.develop.feature.splash.domain.AuthRepository

class IsLoggedInUseCaseImpl(
    private val repository: AuthRepository
) : IsLoggedInUseCase {
    
    override suspend fun execute(): Boolean {
        return repository.isLoggedIn()
    }
}
