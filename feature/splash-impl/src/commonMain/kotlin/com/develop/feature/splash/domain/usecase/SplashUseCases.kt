package com.develop.feature.splash.domain.usecase

class SplashUseCases(
    val authenticate: AuthenticateUseCase,
    val logout: LogoutUseCase,
    val isLoggedIn: IsLoggedInUseCase
)
