package com.develop.feature.splash.di

import com.develop.feature.splash.domain.AuthRepository
import com.develop.feature.splash.domain.usecase.AuthenticateUseCase
import com.develop.feature.splash.domain.usecase.AuthenticateUseCaseImpl
import com.develop.feature.splash.domain.usecase.IsLoggedInUseCase
import com.develop.feature.splash.domain.usecase.IsLoggedInUseCaseImpl
import com.develop.feature.splash.domain.usecase.LogoutUseCase
import com.develop.feature.splash.domain.usecase.LogoutUseCaseImpl
import com.develop.feature.splash.domain.usecase.SplashUseCases
import com.develop.feature.splash.data.api.AuthApi
import com.develop.feature.splash.data.repository.AuthRepositoryImpl
import com.develop.feature.splash.presentation.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val splashModule = module {
    // API
    single { AuthApi(get()) }
    
    // Repository
    single { AuthRepositoryImpl(get(), get(), get()) } bind AuthRepository::class
    
    // UseCases
    single { AuthenticateUseCaseImpl(get()) } bind AuthenticateUseCase::class
    single { LogoutUseCaseImpl(get()) } bind LogoutUseCase::class
    single { IsLoggedInUseCaseImpl(get()) } bind IsLoggedInUseCase::class
    single { SplashUseCases(get(), get(), get()) }
    
    // ViewModel
    viewModel { SplashViewModel(get()) }
}
