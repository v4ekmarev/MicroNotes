package com.develop.feature.profile.di

import com.develop.feature.profile.data.api.ProfileApi
import com.develop.feature.profile.data.repository.ProfileRepositoryImpl
import com.develop.feature.profile.domain.ProfileRepository
import com.develop.feature.profile.domain.usecase.GetProfileUseCase
import com.develop.feature.profile.domain.usecase.GetProfileUseCaseImpl
import com.develop.feature.profile.domain.usecase.ProfileUseCases
import com.develop.feature.profile.domain.usecase.UpdateProfileUseCase
import com.develop.feature.profile.domain.usecase.UpdateProfileUseCaseImpl
import com.develop.feature.profile.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val profileModule = module {
    // API
    single { ProfileApi(get()) }
    
    // Repository
    single { ProfileRepositoryImpl(get()) } bind ProfileRepository::class
    
    // UseCases
    single { GetProfileUseCaseImpl(get()) } bind GetProfileUseCase::class
    single { UpdateProfileUseCaseImpl(get()) } bind UpdateProfileUseCase::class
    single { ProfileUseCases(get(), get()) }
    
    // ViewModel
    viewModel { ProfileViewModel(get()) }
}
